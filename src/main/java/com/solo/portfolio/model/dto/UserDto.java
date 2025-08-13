package com.solo.portfolio.model.dto;

import com.solo.portfolio.model.entity.Gender;
import com.solo.portfolio.model.entity.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String avatarUrl;
    private UserRole role;
    private Gender gender;
    private LocalDateTime birthday;
    private String address;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 