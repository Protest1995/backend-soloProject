package com.solo.portfolio.model.dto;

import lombok.Data;

/**
 * 使用者更新請求（所有欄位皆為可選，未提供則不更新）
 */
@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private String avatarUrl;
    private String gender;     // MALE/FEMALE/OTHER/NOT_SPECIFIED 或小寫
    private String birthday;   // yyyy-MM-dd 或 yyyy/MM/dd
    private String address;
    private String phone;
    private String password;   // 新密碼（可選）
}


