package com.solo.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts", schema = "public")
@Data
@NoArgsConstructor
public class BlogPost {
    @Id
    private String id;

    @Column(length = 1000)
    private String imageUrl;
    
    private Boolean isLocked;
    private Long createdAt;
    
    @Column(length = 100)
    private String categoryKey;
    
    private Integer likes;
    private Integer commentsCount;
    private Integer views;
    private Boolean isFeatured;

    @Column(length = 500)
    private String title;
    
    @Column(length = 500)
    private String titleZh;
    
    @Column(columnDefinition = "TEXT")
    private String excerpt;
    
    @Column(columnDefinition = "TEXT")
    private String excerptZh;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String contentZh;

    private LocalDateTime date;

    @Column(name = "created_at_ts")
    private LocalDateTime createdAtTs;

    @PrePersist
    protected void onCreate() {
        createdAtTs = LocalDateTime.now();
    }
}


