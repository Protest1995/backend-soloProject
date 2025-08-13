package com.solo.portfolio.model.dto;

import lombok.Data;

@Data
public class PortfolioItemRequest {
    private String imageUrl;
    private String title;
    private String titleZh;
    private String categoryKey;
    private Boolean isFeatured;
}
