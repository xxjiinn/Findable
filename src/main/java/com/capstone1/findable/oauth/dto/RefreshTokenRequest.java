package com.capstone1.findable.oauth.dto;

public class RefreshTokenRequest {
    private String refreshToken;

    // 기본 생성자
    public RefreshTokenRequest() {}

    // 파라미터를 받는 생성자 추가
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getters and setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}