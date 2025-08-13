package com.solo.portfolio.security.oauth;

import com.solo.portfolio.model.entity.User;
import com.solo.portfolio.model.entity.UserRole;
import com.solo.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = getAttribute(attributes, "email");
        String name = getAttribute(attributes, "name");
        String picture = getAttribute(attributes, "picture");

        log.info("OAuth2 login via {}: email={}, name={}", registrationId, email, name);

        if (email == null || email.isBlank()) {
            // 對於極少數不回傳 email 的情況，回退為 sub@provider
            String sub = getAttribute(attributes, "sub");
            email = sub + "@" + registrationId + ".oauth";
        }

        // 確保用戶存在或自動註冊
        ensureUserExists(email, name, picture);
        log.info("OAuth2 ensureUserExists finished for email={}.", email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private void ensureUserExists(String email, String name, String avatarUrl) {
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) return;

        // 產生唯一 username（以 email local-part 為基底）
        String baseUsername = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        String candidate = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = baseUsername + suffix;
            suffix++;
        }

        User user = new User();
        user.setUsername(candidate);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(UserRole.USER);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        log.info("Created new OAuth2 user: {} ({})", candidate, email);
    }

    @SuppressWarnings("unchecked")
    private static String getAttribute(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : String.valueOf(value);
    }
}


