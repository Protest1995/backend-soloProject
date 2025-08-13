package com.solo.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_items", schema = "public")
@Data
@NoArgsConstructor
public class PortfolioItem {
    @Id
    private String id;

    @Column(length = 1000)
    private String imageUrl;
    
    @Column(length = 500)
    private String title;
    
    @Column(length = 500)
    private String titleZh;
    
    @Column(length = 100)
    private String categoryKey;
    
    private Integer views;
    private Boolean isFeatured;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


