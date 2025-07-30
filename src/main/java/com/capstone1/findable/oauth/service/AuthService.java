package com.capstone1.findable.oauth.service;

import com.capstone1.findable.Exception.UnauthorizedAccessException;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.BlacklistedToken;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.User.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/** 인증 서비스 - 로그인 및 토큰 관리 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    /** 사용자 로그인 수행 및 JWT 토큰 발급 */
    @Transactional
    public Map<String, String> login(UserDTO.LoginUserDTO loginDTO) {
        log.info("☑️ [LOGIN] Attempt for email: {}", loginDTO.getEmail());

        // 이메일로 사용자 조회
        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email or password"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("⚠️ [LOGIN] Password mismatch for email={}", loginDTO.getEmail());
            throw new UnauthorizedAccessException("Invalid email or password");
        }

        // 1) Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());
        log.info("🎟️ Access Token generated: {}", accessToken);

        // 2) 기존 유효한 Refresh Token이 있으면 재사용, 없거나 만료된 경우 새로 생성
        String refreshToken = refreshTokenRepo.findByUserId(user.getId())
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(rt -> {
                    log.debug("🔄 [REFRESH] Reusing existing token for userId={}", user.getId());
                    return rt.getToken();
                })
                .orElseGet(() -> {
                    String newToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
                    saveNewRefreshToken(user, newToken);
                    log.info("🔑 Refresh Token generated: {}", newToken);
                    return newToken;
                });

        log.info("✅ [LOGIN] Successful for email={}", loginDTO.getEmail());
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    /** 새로운 RefreshToken 저장 */
    private void saveNewRefreshToken(User user, String tokenValue) {
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(refreshTokenValidity / 1000);
        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(tokenValue)
                .deviceId("default")
                .expiryDate(expiry)
                .createdAt(LocalDateTime.now())
                .build();
        refreshTokenRepo.save(newToken);
    }

    /** 사용자 이메일로 ID 조회 */
    @Transactional(readOnly = true)
    public Long getUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
    }

    /** 로그아웃 시 RefreshToken 삭제 및 블랙리스트 등록 */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepo.findByToken(refreshToken).ifPresent(refreshTokenRepo::delete);
        blacklistedTokenRepo.save(BlacklistedToken.builder()
                .token(refreshToken)
                .blacklistedAt(LocalDateTime.now())
                .build());
        log.info("🚪 [LOGOUT] Completed for token={}", refreshToken);
    }
}
