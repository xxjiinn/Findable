package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.dto.RefreshTokenRequest;
import com.capstone1.findable.oauth.entity.BlacklistedToken;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String deviceId = request.getDeviceId();

        logger.info("Received refresh token request for Device ID: {}", deviceId);

        try {
            // 블랙리스트 확인
            if (jwtTokenProvider.isTokenBlacklisted(refreshToken)) {
                logger.warn("Attempted use of blacklisted refresh token: {}", refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Blacklisted refresh token"));
            }

            // 데이터베이스에서 토큰 검색
            Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByTokenAndDeviceId(refreshToken, deviceId);
            if (refreshTokenEntity.isEmpty()) {
                logger.warn("Invalid refresh token or device ID: {}, {}", refreshToken, deviceId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token or device ID"));
            }

            RefreshToken storedToken = refreshTokenEntity.get();

            // 토큰 만료 여부 확인
            if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                logger.warn("Expired refresh token for Device ID: {}", deviceId);
                refreshTokenRepo.delete(storedToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Expired refresh token"));
            }

            // 새 토큰 생성
            String username = storedToken.getUser().getUsername();
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

            // 기존 리프레시 토큰 삭제 및 새로 저장
            refreshTokenRepo.delete(storedToken);
            refreshTokenRepo.save(RefreshToken.builder()
                    .token(newRefreshToken)
                    .user(storedToken.getUser())
                    .deviceId(deviceId)
                    .expiryDate(LocalDateTime.now().plusWeeks(1))
                    .createdAt(LocalDateTime.now())
                    .build());

            logger.info("New tokens issued for user: {}", username);
            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken
            ));

        } catch (Exception e) {
            logger.error("Error during refresh token process: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();

        logger.info("Received Logout Request with Refresh Token: {} and Access Token: {}", refreshToken, accessToken);

        if (refreshToken == null || accessToken == null) {
            logger.warn("Invalid logout request. Tokens are missing.");
            return ResponseEntity.badRequest().body("Tokens are required for logout.");
        }

        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            logger.warn("Invalid Refresh Token.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }

        try {
            // 블랙리스트에 Access Token 추가
            if (jwtTokenProvider.validateToken(accessToken)) {
                blacklistedTokenRepo.save(BlacklistedToken.builder()
                        .token(accessToken)
                        .blacklistedAt(LocalDateTime.now())
                        .build());
            }

            // 블랙리스트에 Refresh Token 추가
            blacklistedTokenRepo.save(BlacklistedToken.builder()
                    .token(refreshToken)
                    .blacklistedAt(LocalDateTime.now())
                    .build());

            // Refresh Token 삭제
            refreshTokenRepo.delete(refreshTokenEntity.get());

            logger.info("Logout successful for Refresh Token: {}", refreshToken);
            return ResponseEntity.ok("Successfully logged out");
        } catch (Exception e) {
            logger.error("Error during logout process: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout");
        }
    }
}