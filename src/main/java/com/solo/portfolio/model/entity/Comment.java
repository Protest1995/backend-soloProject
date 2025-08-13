package com.solo.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id;

    @Column(name = "post_id", length = 64, nullable = false)
    private String postId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(length = 100, nullable = false)
    private String username;

    @Column(name = "avatar_url", length = 500, nullable = false)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "parent_id", length = 36)
    private String parentId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}



