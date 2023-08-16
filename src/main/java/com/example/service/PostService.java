package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.Post;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import com.example.repository.PostEntityRepository;
import com.example.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName){
        // DB에서 유저 찾기
        UserEntity userEntity = userEntityRepository.findByUsername(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // POST 저장
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));

    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId){
        UserEntity userEntity = userEntityRepository.findByUsername(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 포스트가 존재하는지
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId))
        );
        // 수정하려고 하는 사람이 권한이 있는 사람인지
        if(postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("% has no permission", userName));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }
}
