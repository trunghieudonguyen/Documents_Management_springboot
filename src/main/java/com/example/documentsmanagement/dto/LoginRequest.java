package com.example.documentsmanagement.dto;

/**
 * LoginRequest là lớp DTO dùng để nhận dữ liệu đăng nhập từ client.
 * Chỉ chứa username và password (mật khẩu thô sẽ được hash trong Oracle).
 */
public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
