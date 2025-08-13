package com.solo.portfolio.config;

import com.solo.portfolio.model.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.portfolio.model.entity.PortfolioItem;
import com.solo.portfolio.model.entity.BlogPost;
import com.solo.portfolio.repository.PortfolioItemRepository;
import com.solo.portfolio.repository.BlogPostRepository;
import com.solo.portfolio.model.entity.UserRole;
import com.solo.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PortfolioItemRepository portfolioRepo;
    private final BlogPostRepository blogRepo;
    
    @Override
    public void run(String... args) throws Exception {
        // 檢查是否已存在管理員用戶
        if (!userRepository.existsByUsername("admin")) {
            log.info("創建默認管理員用戶...");
            
            User adminUser = new User();
            adminUser.setId("admin-001");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("password123"));
            adminUser.setAvatarUrl("/images/profile.jpg");
            adminUser.setRole(UserRole.SUPER_USER);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(adminUser);
            log.info("默認管理員用戶創建成功: admin/password123");
        } else {
            log.info("管理員用戶已存在，跳過初始化");
        }

        // 將現有 JSON 靜態資料匯入資料庫（只在資料庫為空時）
        ObjectMapper mapper = new ObjectMapper();

        java.util.function.Function<String, java.util.List<java.util.Map<String,Object>>> readList = (path) -> {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(path);
                if (java.nio.file.Files.exists(p)) {
                    return mapper.readValue(java.nio.file.Files.newInputStream(p), new TypeReference<java.util.List<java.util.Map<String,Object>>>(){});
                }
            } catch (Exception ignored) {}
            try {
                var is = getClass().getClassLoader().getResourceAsStream(path.substring(path.lastIndexOf('/')+1));
                if (is != null) {
                    return mapper.readValue(is, new TypeReference<java.util.List<java.util.Map<String,Object>>>(){});
                }
            } catch (Exception ignored) {}
            return java.util.Collections.emptyList();
        };
        if (portfolioRepo.count() == 0) {
            try {
                var list = readList.apply("../src/data/staticPortfolio.json");
                if (list != null && !list.isEmpty()) {
                    for (var m : list) {
                        PortfolioItem item = new PortfolioItem();
                        item.setId((String)m.get("id"));
                        item.setImageUrl((String)m.get("imageUrl"));
                        item.setTitle((String)m.get("title"));
                        item.setTitleZh((String)m.get("titleZh"));
                        item.setCategoryKey((String)m.get("categoryKey"));
                        Object views = m.get("views");
                        item.setViews(views instanceof Number ? ((Number)views).intValue() : 0);
                        Object featured = m.get("isFeatured");
                        item.setIsFeatured(featured instanceof Boolean ? (Boolean)featured : false);
                        portfolioRepo.save(item);
                    }
                    log.info("已匯入 Portfolio 靜態資料 {} 筆", portfolioRepo.count());
                } else { log.warn("找不到 ../src/data/staticPortfolio.json 與 classpath:staticPortfolio.json"); }
            } catch (Exception e) {
                log.warn("匯入 Portfolio 靜態資料失敗", e);
            }
        }

        if (blogRepo.count() == 0) {
            try {
                var list = readList.apply("../src/data/staticPosts.json");
                if (list != null && !list.isEmpty()) {
                    for (var m : list) {
                        BlogPost p = new BlogPost();
                        p.setId((String)m.get("id"));
                        p.setImageUrl((String)m.get("imageUrl"));
                        p.setIsLocked((Boolean)m.getOrDefault("isLocked", false));
                        Object ca = m.get("createdAt");
                        p.setCreatedAt(ca instanceof Number ? ((Number)ca).longValue() : 0L);
                        p.setCategoryKey((String)m.get("categoryKey"));
                        p.setLikes(((Number)m.getOrDefault("likes",0)).intValue());
                        p.setCommentsCount(((Number)m.getOrDefault("commentsCount",0)).intValue());
                        p.setViews(((Number)m.getOrDefault("views",0)).intValue());
                        p.setIsFeatured((Boolean)m.getOrDefault("isFeatured", false));
                        p.setTitle((String)m.get("title"));
                        p.setTitleZh((String)m.get("titleZh"));
                        p.setExcerpt((String)m.get("excerpt"));
                        p.setExcerptZh((String)m.get("excerptZh"));
                        p.setContent((String)m.get("content"));
                        p.setContentZh((String)m.get("contentZh"));
                        blogRepo.save(p);
                    }
                    log.info("已匯入 Blog 靜態資料 {} 筆", blogRepo.count());
                } else { log.warn("找不到 ../src/data/staticPosts.json 與 classpath:staticPosts.json"); }
            } catch (Exception e) {
                log.warn("匯入 Blog 靜態資料失敗", e);
            }
        }
    }
} 