package com.solo.portfolio.controller;

import com.solo.portfolio.model.dto.AuthRequest;
import com.solo.portfolio.model.dto.AuthResponse;
import com.solo.portfolio.model.dto.RegisterRequest;
import com.solo.portfolio.service.AuthService;
import com.solo.portfolio.model.dto.UpdateUserRequest;
import com.solo.portfolio.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Auth", description = "Authentication and user account endpoints")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用戶登入
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }
    
    /**
     * 用戶註冊
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Register failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchange a refresh token for a new access token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }
    
    /**
     * 用戶登出
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate the provided refresh token (if any)")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        try {
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken != null) {
                authService.logout(refreshToken);
            }
            return ResponseEntity.ok(new AuthResponse(true, "登出成功", null, null, null));
        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }
    
    /**
     * 刷新令牌請求 DTO
     */
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * 觸發 Google OAuth2 授權（後端導向）
     */
    @GetMapping("/oauth2/authorize/google")
    @Operation(summary = "Start Google OAuth2 flow")
    public void redirectToGoogle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 轉向 Spring Security 預設的授權端點（加上 context-path）
        String location = request.getContextPath() + "/oauth2/authorization/google";
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", location);
    }

    /**
     * OAuth2 成功回調（由成功處理器重導到這裡，攜帶 token）
     */
    @GetMapping("/oauth2/success")
    @Operation(summary = "OAuth2 success callback")
    public ResponseEntity<AuthResponse> oauth2Success(@RequestParam("token") String token,
                                                      @RequestParam("refreshToken") String refreshToken) {
        try {
            // 從 token 解析用戶名，查出資料
            String username = authService.getUsernameFromAccessToken(token);
            var user = authService.findUserByUsername(username);
            return ResponseEntity.ok(new AuthResponse(true, "登入成功", token, refreshToken, authService.toDto(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }

    /**
     * OAuth2 失敗回調
     */
    @GetMapping("/oauth2/failure")
    @Operation(summary = "OAuth2 failure callback")
    public ResponseEntity<AuthResponse> oauth2Failure(@RequestParam(value = "error", required = false) String error) {
        String message = error != null ? error : "OAuth2 認證失敗";
        return ResponseEntity.badRequest().body(new AuthResponse(false, message, null, null, null));
    }

    /**
     * 取得目前登入使用者（以 Authorization Bearer Token 解析）
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<AuthResponse> me(@RequestHeader(name = "Authorization", required = false) String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new AuthResponse(false, "未提供憑證", null, null, null));
            }
            String token = authorization.substring("Bearer ".length());
            String username = authService.getUsernameFromAccessToken(token);
            var user = authService.findUserByUsername(username);
            return ResponseEntity.ok(new AuthResponse(true, "OK", null, null, authService.toDto(user)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(false, "無效或過期的憑證", null, null, null));
        }
    }

    /**
     * 更新目前登入使用者
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME_NAME)
    public ResponseEntity<AuthResponse> updateMe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody UpdateUserRequest request) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new AuthResponse(false, "未提供憑證", null, null, null));
            }
            String token = authorization.substring("Bearer ".length());
            String username = authService.getUsernameFromAccessToken(token);
            var updated = authService.updateCurrentUser(username, request);
            return ResponseEntity.ok(new AuthResponse(true, "更新成功", null, null, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage(), null, null, null));
        }
    }
} 