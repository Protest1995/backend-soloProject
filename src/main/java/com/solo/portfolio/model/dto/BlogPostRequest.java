package com.solo.portfolio.model.dto;

import lombok.Data;

@Data
public class BlogPostRequest {
    private String imageUrl;
    private Boolean isLocked;
    private String categoryKey;
    private Boolean isFeatured;
    private String title;
    private String titleZh;
    private String excerpt;
    private String excerptZh;
    private String content;
    private String contentZh;
}
