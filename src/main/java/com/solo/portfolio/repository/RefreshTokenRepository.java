package com.solo.portfolio.repository;

import com.solo.portfolio.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    
    /**
     * 根據令牌查找刷新令牌
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * 根據用戶ID查找刷新令牌
     */
    Optional<RefreshToken> findByUserId(String userId);
    
    /**
     * 刪除過期的刷新令牌
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
    
    /**
     * 根據用戶ID刪除刷新令牌
     */
    void deleteByUserId(String userId);
} 