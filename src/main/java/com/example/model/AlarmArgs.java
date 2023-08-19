package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmArgs {
    // 알람을 발생시킨 사람
    private Integer fromUserId;
    // 알람이 발생한 주체에 대한 ID
    // 게시물에 댓글이 달린 경우 게시글 ID
    private Integer targetId;
}
