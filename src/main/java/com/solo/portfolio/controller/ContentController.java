package com.solo.portfolio.controller;

import com.solo.portfolio.model.dto.BlogPostRequest;
import com.solo.portfolio.model.dto.PortfolioItemRequest;
import com.solo.portfolio.model.entity.BlogPost;
import com.solo.portfolio.model.entity.PortfolioItem;
import com.solo.portfolio.service.ContentService;
import com.solo.portfolio.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Content", description = "Portfolio and blog content management")
public class ContentController {
    private final ContentService contentService;

    // Portfolio endpoints
    @GetMapping("/portfolio")
    @Operation(summary = "List portfolio items")
    public ResponseEntity<List<PortfolioItem>> getPortfolio() {
        return ResponseEntity.ok(contentService.getAllPortfolioItems());
    }

    @GetMapping("/portfolio/{id}")
    @Operation(summary = "Get a portfolio item by id")
    public ResponseEntity<PortfolioItem> getPortfolioItem(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getPortfolioItemById(id));
    }

    @PostMapping("/portfolio")
    @Operation(summary = "Create a portfolio item")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<PortfolioItem> createPortfolioItem(@RequestBody PortfolioItemRequest request) {
        return ResponseEntity.ok(contentService.createPortfolioItem(request));
    }

    @PutMapping("/portfolio/{id}")
    @Operation(summary = "Update a portfolio item")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<PortfolioItem> updatePortfolioItem(@PathVariable String id, @RequestBody PortfolioItemRequest request) {
        return ResponseEntity.ok(contentService.updatePortfolioItem(id, request));
    }

    @DeleteMapping("/portfolio/{id}")
    @Operation(summary = "Delete a portfolio item")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<Void> deletePortfolioItem(@PathVariable String id) {
        contentService.deletePortfolioItem(id);
        return ResponseEntity.ok().build();
    }

    // Blog posts endpoints
    @GetMapping("/posts")
    @Operation(summary = "List blog posts")
    public ResponseEntity<List<BlogPost>> getPosts() {
        return ResponseEntity.ok(contentService.getAllPosts());
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get blog post by id")
    public ResponseEntity<BlogPost> getPost(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getPostById(id));
    }

    @PostMapping("/posts")
    @Operation(summary = "Create blog post")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(contentService.createPost(request));
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "Update blog post")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<BlogPost> updatePost(@PathVariable String id, @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(contentService.updatePost(id, request));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete blog post")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        contentService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}


