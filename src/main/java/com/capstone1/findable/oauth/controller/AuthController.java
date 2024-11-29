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
        logger.info("Received Refresh Token: {}", refreshToken);

        // Step 1: Refresh Token의 블랙리스트 확인
        if (jwtTokenProvider.isTokenBlacklisted(refreshToken)) {
            logger.warn("Refresh Token is blacklisted.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh Token is blacklisted. Please log in again."));
        } ////

        // Step 2: 데이터베이스에서 Refresh Token 확인
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            logger.warn("Refresh Token not found in the database.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh Token not found. Please log in again."));
        }

        RefreshToken storedToken = refreshTokenEntity.get();

        // Step 3: 데이터베이스의 만료 시간 확인
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Refresh Token has expired. Expiry Date: {}", storedToken.getExpiryDate());
            refreshTokenRepo.delete(storedToken); // 만료된 토큰 삭제
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh Token has expired. Please log in again."));
        }

        // Step 4: Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.warn("JWT Token validation failed.");
            refreshTokenRepo.delete(storedToken); // 변조된 토큰 삭제
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or reused Refresh Token."));
        }

        // Step 5: 새로운 Access Token 및 Refresh Token 발급
        User user = storedToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // 기존 Refresh Token 삭제 및 새 토큰 저장
        refreshTokenRepo.delete(storedToken); // 기존 토큰 제거
        refreshTokenRepo.save(RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusWeeks(1))
                .createdAt(LocalDateTime.now())
                .build());

        logger.info("Generated new tokens for user: {}", user.getUsername());
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        ));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        logger.info("Received Logout Request with Refresh Token: {}", refreshToken);

        // Refresh Token이 데이터베이스에 존재하지 않을 경우
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            logger.warn("Refresh Token not found in the database.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }

        // Refresh Token을 블랙리스트에 추가
        blacklistedTokenRepo.save(BlacklistedToken.builder()
                .token(refreshToken)
                .blacklistedAt(LocalDateTime.now())
                .build());
        logger.info("Refresh Token added to blacklist: {}", refreshToken);

        // 기존 Refresh Token 삭제
        refreshTokenRepo.delete(refreshTokenEntity.get());

        return ResponseEntity.ok("Successfully logged out");
    }

}
