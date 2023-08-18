package com.example.controller;

import com.example.dto.request.PostCommentRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @WithMockUser
    @Test
    void 포스트삭제() throws Exception {
        // post api까지 넣어줌
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 포스트삭제시_로그인_하지_않은_경우() throws Exception {
        // post api까지 넣어줌
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 포스트삭제시_작성자와_요청자가_다른경우() throws Exception {
        // return 해줄게 없으면 doThrow로 시작
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        // post api까지 넣어줌
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 포스트삭제시_포스트가_존재하지_않는_경우() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

        // post api까지 넣어줌
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    void 게시글목록() throws Exception {
        //mocking
        when(postService.list(any())).thenReturn(Page.empty());

        // post api까지 넣어줌
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 게시글목록_요청시_로그인_하지_않은_경우() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        // post api까지 넣어줌
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 나의_게시글목록() throws Exception {
        //mocking
        when(postService.my(any(), any())).thenReturn(Page.empty());

        // post api까지 넣어줌
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 나의_게시글목록_요청시_로그인_하지_않은_경우() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        // post api까지 넣어줌
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 좋아요기능() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 좋아요_클릭시_로그인_하지_않은_경우() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 좋아요_클릭시_게시물이_없는_경우() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    void 댓글기능() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("commnet")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void 댓글_작성시_로그인_하지_않은_경우() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("commnet")))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void 댓글_작성시_게시물이_없는_경우() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(), any(), any());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("commnet")))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }


}
