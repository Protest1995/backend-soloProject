package com.solo.portfolio.security.oauth;

import com.solo.portfolio.model.entity.RefreshToken;
import com.solo.portfolio.model.entity.User;
import com.solo.portfolio.model.entity.UserRole;
import com.solo.portfolio.repository.RefreshTokenRepository;
import com.solo.portfolio.repository.UserRepository;
import com.solo.portfolio.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${oauth2.frontend-success-url:http://localhost:5173/login}")
    private String frontendSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = String.valueOf(oAuth2User.getAttributes().getOrDefault("email", ""));
        String sub = String.valueOf(oAuth2User.getAttributes().getOrDefault("sub", "oauthUser"));
        String preferredUsername = !email.isEmpty() ? email : sub;

        // 確保用戶存在
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            String baseUsername = preferredUsername.contains("@") ? preferredUsername.substring(0, preferredUsername.indexOf('@')) : preferredUsername;
            String candidate = baseUsername;
            int suffix = 1;
            while (userRepository.existsByUsername(candidate)) {
                candidate = baseUsername + suffix;
                suffix++;
            }
            user = new User();
            user.setUsername(candidate);
            user.setEmail(!email.isEmpty() ? email : (sub + "@google.oauth"));
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole(UserRole.USER);
            Object picture = oAuth2User.getAttributes().get("picture");
            if (picture != null) user.setAvatarUrl(String.valueOf(picture));
            user = userRepository.save(user);
            log.info("Created user in success handler: {} ({})", user.getUsername(), user.getEmail());
        }

        String subject = user.getUsername();
        String accessToken = jwtTokenProvider.generateAccessToken(subject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(subject);

        // 保存刷新令牌
        try { refreshTokenRepository.deleteByUserId(user.getId()); } catch (Exception ignored) {}
        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refreshToken);
        rt.setExpiresAt(LocalDateTime.now().plusSeconds(7 * 24 * 60 * 60));
        refreshTokenRepository.save(rt);

        // 前端為 SPA，將 token 以 URL fragment/hash 返回
        String redirectUrl = frontendSuccessUrl
                + "#token=" + urlEncode(accessToken)
                + "&refreshToken=" + urlEncode(refreshToken);

        log.info("OAuth2 success for user={} redirecting to frontend.", subject);
        response.sendRedirect(redirectUrl);
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}


