package com.capstone1.findable.oauth.dto;

public class RefreshTokenRequest {
    private String refreshToken;
    private String deviceId; // 디바이스 ID 추가
    private String accessToken; // Access Token 필드 추가

    // 기본 생성자
    public RefreshTokenRequest() {}

    // 파라미터를 받는 생성자
    public RefreshTokenRequest(String refreshToken, String deviceId, String accessToken) {
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.accessToken = accessToken;
    }

    // Getters and setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccessToken() { // 추가된 getter
        return accessToken;
    }

    public void setAccessToken(String accessToken) { // 추가된 setter
        this.accessToken = accessToken;
    }
}
