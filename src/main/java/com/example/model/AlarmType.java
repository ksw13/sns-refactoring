package com.example.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    NEW_COMMENT_ON_POST("new Comment"),
    NEW_LIKE_ON_POST("new like"),
    ;

    private final String alarmText;
}
