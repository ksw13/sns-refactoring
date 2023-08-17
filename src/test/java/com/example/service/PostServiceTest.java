package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.fixture.PostEntityFixture;
import com.example.fixture.UserEntityFixture;
import com.example.model.entity.LikeEntity;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import com.example.repository.LikeEntityRepository;
import com.example.repository.PostEntityRepository;
import com.example.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private LikeEntityRepository likeEntityRepository;

    @Test
    void 포스트작성이_정상적인_경우(){
        String title="title";
        String body="body";
        String userName="userName";

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

    @Test
    void 포스트작성시_요청한_유저가_존재하지_않는_경우(){
        String title="title";
        String body="body";
        String userName="userName";

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정이_정상적인_경우(){
        String title="title";
        String body="body";
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지_않는_경우(){
        String title="title";
        String body="body";
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_존재하지_않는_경우(){
        String title="title";
        String body="body";
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();
        UserEntity writer = UserEntityFixture.get("userName","password", 2);

        // 권한이 다른 writer를 return
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트삭제가_정상적인_경우(){
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete("userName",1));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지_않는_경우(){
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete("userName",1));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_존재하지_않는_경우(){
        String userName="userName";
        Integer postId =1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();
        UserEntity writer = UserEntityFixture.get("userName","password", 2);

        // 권한이 다른 writer를 return
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName,1));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 게시글목록_요청이_정상적인_경우(){
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 나의_게시글목록_요청이_정상적인_경우(){
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUser(userEntity, pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.my("",pageable));
    }

    @Test
    void 좋아요가_성공한_경우(){
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeEntityRepository.findByUserAndPost(user,post)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> postService.like(postId,username));
    }

    @Test
    void 좋아요시_포스트가_존재하지_않는_경우(){
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());
        when(likeEntityRepository.findByUserAndPost(user,post)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.like(postId,username));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 좋아요시_이미_좋아요를_누른_경우(){
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);
        LikeEntity like = mock(LikeEntity.class);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeEntityRepository.findByUserAndPost(user,post)).thenReturn(Optional.of(like));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.like(postId,username));
        Assertions.assertEquals(ErrorCode.ALREADY_LIKE, e.getErrorCode());
    }

}
