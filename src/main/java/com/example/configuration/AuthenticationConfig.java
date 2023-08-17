package com.example.configuration;

import com.example.configuration.filter.JwtTokenFilter;
import com.example.exception.CustomAuthenticationEntryPoint;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    // http 요청이 들어왔을 때, security를 어떻게 설정할 것인가를 정의하는 곳

    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;

    // "/api"로 시작하는 것만 통과를 시킨다.
    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().regexMatchers("^(?!/api/).*");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // filter(JwtTokenFilter)를 새롭게 정의하여 들어온 token이 어떤 user를 가르키는지를 체크
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                // filter에서 발생한 에러는 CustomAuthenticationEntryPoint에서 처리
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                ;
    }
}
