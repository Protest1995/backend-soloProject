package com.solo.portfolio.controller;

import com.solo.portfolio.model.dto.CommentRequest;
import com.solo.portfolio.model.dto.CommentResponse;
import com.solo.portfolio.service.AuthService;
import com.solo.portfolio.service.CommentService;
import com.solo.portfolio.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Comments", description = "Blog post comments")
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    @GetMapping("/post/{postId}")
    @Operation(summary = "List comments for a post")
    public ResponseEntity<List<CommentResponse>> getByPost(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PostMapping
    @Operation(summary = "Add a comment to a post")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<CommentResponse> add(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @RequestBody CommentRequest req) {
        String username = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try { username = authService.getUsernameFromAccessToken(token); } catch (Exception ignored) {}
        }
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(commentService.addComment(username, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment (admin only)")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<Void> delete(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @PathVariable String id) {
        String username = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try { username = authService.getUsernameFromAccessToken(token); } catch (Exception ignored) {}
        }
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        // 僅允許管理員/超級用戶
        var user = authService.findUserByUsername(username);
        var role = user.getRole();
        if (role == null || !("ADMIN".equals(role.name()) || "SUPER_USER".equals(role.name()))) {
            return ResponseEntity.status(403).build();
        }
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}


