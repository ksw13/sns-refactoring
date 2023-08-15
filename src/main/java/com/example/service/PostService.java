package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
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
}
