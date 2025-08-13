package com.solo.portfolio.service;

import com.solo.portfolio.model.dto.AuthRequest;
import com.solo.portfolio.model.dto.AuthResponse;
import com.solo.portfolio.model.dto.RegisterRequest;
import com.solo.portfolio.model.dto.UserDto;
import com.solo.portfolio.model.dto.UpdateUserRequest;
import com.solo.portfolio.model.entity.RefreshToken;
import com.solo.portfolio.model.entity.User;
import com.solo.portfolio.model.entity.UserRole;
import com.solo.portfolio.model.entity.Gender;
import com.solo.portfolio.repository.RefreshTokenRepository;
import com.solo.portfolio.repository.UserRepository;
import com.solo.portfolio.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    /**
     * 用戶登入
     */
    @Transactional
    public AuthResponse login(AuthRequest request) {
        try {
            // 驗證用戶憑證
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // 生成令牌
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());
            
            // 保存刷新令牌
            saveRefreshToken(request.getUsername(), refreshToken);
            
            // 獲取用戶信息
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
            return new AuthResponse(
                true,
                "登入成功",
                accessToken,
                refreshToken,
                convertToDto(user)
            );
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            // 提供更具體的錯誤訊息
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("Bad credentials")) {
                    throw new RuntimeException("用戶名或密碼錯誤");
                } else if (errorMessage.contains("User not found")) {
                    throw new RuntimeException("用戶名不存在");
                } else if (errorMessage.contains("Authentication failed")) {
                    throw new RuntimeException("認證失敗，請檢查用戶名和密碼");
                } else {
                    throw new RuntimeException("登入失敗：" + errorMessage);
                }
            } else {
                throw new RuntimeException("登入失敗，請檢查用戶名和密碼");
            }
        }
    }

    public String getUsernameFromAccessToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }

    public UserDto toDto(User user) {
        return convertToDto(user);
    }
    
    /**
     * 用戶註冊
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 驗證密碼確認
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("密碼確認不匹配");
        }
        
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用戶名「" + request.getUsername() + "」已存在，請選擇其他用戶名");
        }
        
        // 檢查郵箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("郵箱「" + request.getEmail() + "」已被使用，請使用其他郵箱");
        }
        
        // 創建新用戶
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setAvatarUrl("/images/profile.jpg"); // 默認頭像
        
        User savedUser = userRepository.save(user);
        
        // 生成令牌（直接使用使用者名稱，避免 UserDetails 轉型問題）
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername());
        
        // 保存刷新令牌
        saveRefreshToken(savedUser.getUsername(), refreshToken);
        
        return new AuthResponse(
            true,
            "註冊成功",
            accessToken,
            refreshToken,
            convertToDto(savedUser)
        );
    }
    
    /**
     * 刷新令牌
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // 驗證刷新令牌
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new RuntimeException("無效的刷新令牌");
            }
            
            // 查找存儲的刷新令牌
            RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("刷新令牌不存在"));
            
            // 檢查是否過期
            if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                refreshTokenRepository.delete(storedToken);
                throw new RuntimeException("刷新令牌已過期");
            }
            
            // 依 userId 取得用戶資料
            User user = userRepository.findById(storedToken.getUserId())
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
            // 生成新的令牌
            String newAccessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(user.getUsername(), "")
            );
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
            
            // 更新存儲的刷新令牌
            storedToken.setToken(newRefreshToken);
            storedToken.setExpiresAt(LocalDateTime.now().plusSeconds(7 * 24 * 60 * 60)); // 7天
            refreshTokenRepository.save(storedToken);
            
            return new AuthResponse(
                true,
                "令牌刷新成功",
                newAccessToken,
                newRefreshToken,
                convertToDto(user)
            );
            
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("令牌刷新失敗");
        }
    }
    
    /**
     * 用戶登出
     */
    @Transactional
    public void logout(String refreshToken) {
        try {
            // 刪除刷新令牌
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        } catch (Exception e) {
            log.error("Logout failed", e);
        }
    }
    
    /**
     * 保存刷新令牌
     */
    private void saveRefreshToken(String username, String token) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        // 刪除舊的刷新令牌
        refreshTokenRepository.deleteByUserId(user.getId());
        
        // 創建新的刷新令牌
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(7 * 24 * 60 * 60)); // 7天
        
        refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * 轉換用戶實體為 DTO
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole());
        dto.setGender(user.getGender());
        dto.setBirthday(user.getBirthday());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    /**
     * 更新當前使用者基本資料
     */
    @Transactional
    public UserDto updateCurrentUser(String usernameFromToken, UpdateUserRequest req) {
        User user = userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        if (req.getUsername() != null && !req.getUsername().isBlank() && !req.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.getUsername())) {
                throw new RuntimeException("用戶名已存在");
            }
            user.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank() && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new RuntimeException("郵箱已存在");
            }
            user.setEmail(req.getEmail());
        }
        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getGender() != null) {
            try {
                String normalized = req.getGender().toUpperCase();
                user.setGender(Gender.valueOf(normalized));
            } catch (Exception ignored) {}
        }
        if (req.getBirthday() != null) {
            try {
                String normalized = req.getBirthday().replace('/', '-');
                user.setBirthday(java.time.LocalDate.parse(normalized).atStartOfDay());
            } catch (Exception ignored) {
                user.setBirthday(null);
            }
        }
        if (req.getAddress() != null) {
            user.setAddress(req.getAddress());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            // 加密新密碼
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(user);
        return convertToDto(user);
    }
} 