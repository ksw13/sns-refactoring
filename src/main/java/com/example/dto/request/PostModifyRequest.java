package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostModifyRequest {
    String title;
    String body;
}
