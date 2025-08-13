package com.solo.portfolio.model.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String postId;
    private String text;
    private String parentId; // nullable
}



