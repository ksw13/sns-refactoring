package com.example.controller;

import com.example.dto.request.PostCreateRequest;
import com.example.dto.request.PostModifyRequest;
import com.example.dto.request.UserJoinRequest;
import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.fixture.PostEntityFixture;
import com.example.model.Post;
import com.example.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    // 인증된 유저
    @WithMockUser
    @Test
    void 포스트작성() throws Exception {
        String title="title";
        String body="body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    // 익명의 유저로 로그인 했을 경우를 annotation을 통해 나타냄
    @WithAnonymousUser
    @Test
    void 포스트작성시_로그인하지_않은_경우() throws Exception {
        String title="title";
        String body="body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 포스트수정() throws Exception {
        String title="title";
        String body="body";

        when(postService.modify(eq(title), eq(body), any(), any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get("userName",1,1)));

        // post api까지 넣어줌
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 포스트수정시_로그인하지_않은_경우() throws Exception {
        String title="title";
        String body="body";

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 포스트수정시_본인이_작성한_글이_아닌_경우() throws Exception {
        String title="title";
        String body="body";

        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        // post api까지 넣어줌
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 포스트수정시_수정하려는_글이_없는_경우() throws Exception {
        String title="title";
        String body="body";

        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        // post api까지 넣어줌
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
}
