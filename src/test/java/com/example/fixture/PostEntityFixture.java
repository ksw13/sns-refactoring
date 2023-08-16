package com.example.fixture;

import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId){
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(userName);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);
        return result;
    }
}
