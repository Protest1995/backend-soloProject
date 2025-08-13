package com.solo.portfolio.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String avatarUrl;
    private String date; // ISO string for frontend
    private String text;
    private String parentId;
}



