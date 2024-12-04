package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.dto.RefreshTokenRequest;
import com.capstone1.findable.oauth.entity.BlacklistedToken;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String deviceId = request.getDeviceId();

        logger.info("Received refresh token request for Device ID: {}", deviceId);

        try {
            // Refresh Token 검증
            Optional<RefreshToken> storedTokenOpt = refreshTokenRepo.findByTokenAndDeviceId(refreshToken, deviceId);
            if (storedTokenOpt.isEmpty() || !jwtTokenProvider.validateToken(refreshToken)) {
                logger.warn("Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            RefreshToken storedToken = storedTokenOpt.get();
            if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                logger.warn("Refresh Token expired for Device ID: {}", deviceId);
                refreshTokenRepo.delete(storedToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            // Access Token 생성
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);

            // Refresh Token 재발급 조건 확인
            String newRefreshToken = refreshToken;
            if (storedToken.getExpiryDate().minusDays(1).isBefore(LocalDateTime.now())) { // 만료 1일 전 새로 발급
                newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
                refreshTokenRepo.save(RefreshToken.builder()
                        .token(newRefreshToken)
                        .user(storedToken.getUser())
                        .deviceId(deviceId)
                        .expiryDate(LocalDateTime.now().plusWeeks(1))
                        .createdAt(LocalDateTime.now())
                        .build());
                refreshTokenRepo.delete(storedToken); // 기존 토큰 삭제
            }

            logger.info("New tokens issued for user: {}", username);
            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken
            ));

        } catch (Exception e) {
            logger.error("Error during refresh token process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();

        logger.info("Received Logout Request with Refresh Token: {} and Access Token: {}", refreshToken, accessToken);

        try {
            // 1. Refresh Token 검증 및 삭제
            Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
            if (refreshTokenEntity.isPresent()) {
                refreshTokenRepo.delete(refreshTokenEntity.get());
                logger.info("Refresh Token deleted successfully.");
            } else {
                logger.warn("Invalid Refresh Token.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
            }

            // 2. Access Token 블랙리스트 추가 (선택 사항)
            if (jwtTokenProvider.validateToken(accessToken)) {
                logger.info("Access Token will not be blacklisted for this implementation."); // 블랙리스트 최적화
            }

            // 3. 클라이언트 쿠키 제거
            removeCookie(response, "accessToken");
            removeCookie(response, "refreshToken");

            logger.info("Logout successful.");
            return ResponseEntity.ok("Successfully logged out");
        } catch (Exception e) {
            logger.error("Error during logout process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout");
        }
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }

}