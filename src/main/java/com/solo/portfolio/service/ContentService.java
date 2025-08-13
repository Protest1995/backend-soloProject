package com.solo.portfolio.service;

import com.solo.portfolio.model.dto.BlogPostRequest;
import com.solo.portfolio.model.dto.PortfolioItemRequest;
import com.solo.portfolio.model.entity.BlogPost;
import com.solo.portfolio.model.entity.PortfolioItem;
import com.solo.portfolio.repository.BlogPostRepository;
import com.solo.portfolio.repository.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final BlogPostRepository blogPostRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    // Blog Post methods
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    public BlogPost createPost(BlogPostRequest request) {
        BlogPost post = new BlogPost();
        post.setId(UUID.randomUUID().toString());
        post.setImageUrl(request.getImageUrl());
        post.setIsLocked(request.getIsLocked() != null ? request.getIsLocked() : false);
        post.setCategoryKey(request.getCategoryKey());
        post.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        post.setTitle(request.getTitle());
        post.setTitleZh(request.getTitleZh());
        post.setExcerpt(request.getExcerpt());
        post.setExcerptZh(request.getExcerptZh());
        post.setContent(request.getContent());
        post.setContentZh(request.getContentZh());
        post.setLikes(0);
        post.setCommentsCount(0);
        post.setViews(0);
        post.setDate(LocalDateTime.now());
        post.setCreatedAt(System.currentTimeMillis());
        
        return blogPostRepository.save(post);
    }

    public BlogPost updatePost(String id, BlogPostRequest request) {
        BlogPost post = blogPostRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        post.setImageUrl(request.getImageUrl());
        post.setIsLocked(request.getIsLocked() != null ? request.getIsLocked() : post.getIsLocked());
        post.setCategoryKey(request.getCategoryKey());
        post.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : post.getIsFeatured());
        post.setTitle(request.getTitle());
        post.setTitleZh(request.getTitleZh());
        post.setExcerpt(request.getExcerpt());
        post.setExcerptZh(request.getExcerptZh());
        post.setContent(request.getContent());
        post.setContentZh(request.getContentZh());
        
        return blogPostRepository.save(post);
    }

    public void deletePost(String id) {
        blogPostRepository.deleteById(id);
    }

    public BlogPost getPostById(String id) {
        return blogPostRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog post not found"));
    }

    // Portfolio Item methods
    public List<PortfolioItem> getAllPortfolioItems() {
        return portfolioItemRepository.findAll();
    }

    public PortfolioItem createPortfolioItem(PortfolioItemRequest request) {
        PortfolioItem item = new PortfolioItem();
        item.setId(UUID.randomUUID().toString());
        item.setImageUrl(request.getImageUrl());
        item.setTitle(request.getTitle());
        item.setTitleZh(request.getTitleZh());
        item.setCategoryKey(request.getCategoryKey());
        item.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        item.setViews(0);
        item.setDate(LocalDateTime.now());
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        return portfolioItemRepository.save(item);
    }

    public PortfolioItem updatePortfolioItem(String id, PortfolioItemRequest request) {
        PortfolioItem item = portfolioItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Portfolio item not found"));
        
        item.setImageUrl(request.getImageUrl());
        item.setTitle(request.getTitle());
        item.setTitleZh(request.getTitleZh());
        item.setCategoryKey(request.getCategoryKey());
        item.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : item.getIsFeatured());
        item.setUpdatedAt(LocalDateTime.now());
        
        return portfolioItemRepository.save(item);
    }

    public void deletePortfolioItem(String id) {
        portfolioItemRepository.deleteById(id);
    }

    public PortfolioItem getPortfolioItemById(String id) {
        return portfolioItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Portfolio item not found"));
    }
}
