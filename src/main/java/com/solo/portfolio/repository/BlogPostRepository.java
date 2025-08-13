package com.solo.portfolio.repository;

import com.solo.portfolio.model.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, String> {}


