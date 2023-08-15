package com.example.configuration.filter;

import com.example.model.User;
import com.example.service.UserService;
import com.example.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    // claims에 넣어준 username을 꺼낸 후, user가 유효한 지 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // header 추출
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")){
            log.error("Error occurs while getting header");
            // filter 이어서 수행
            filterChain.doFilter(request, response);
            return ;
        }
        try{
            final String token = header.split(" ")[1].trim();
            // 토큰이 만료 되었는지 검사
            if(JwtTokenUtils.isExpired(token, key)){
                log.error("key is expired");
                filterChain.doFilter(request, response);
                return ;
            }

            // userName 추출
            String userName = JwtTokenUtils.getUserName(token, key);

            // userName 유효성 검사
            User user = userService.loadUserByUserName(userName);

            // principal -> 유저가 누구인지를 넣어줌
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            // request 정보 넣어줌
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch(RuntimeException e){
            log.error("Error occurs while validating {}", e.toString());
            filterChain.doFilter(request,response);
            return ;
        }
        filterChain.doFilter(request,response);

    }
}
