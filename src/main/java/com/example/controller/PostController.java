package com.example.controller;

import com.example.dto.request.PostCreateRequest;
import com.example.dto.response.Response;
import com.example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(PostCreateRequest request){
        postService.create(request.getTitle(), request.getBody(),"");

        return Response.success();
    }
}
