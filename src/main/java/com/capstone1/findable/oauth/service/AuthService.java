package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.User.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;

    public Map<String, String> login(UserDTO.LoginUserDTO loginDTO) {
        logger.info("☑️ [LOGIN] Attempt for email: {}", loginDTO.getEmail());

        // 이메일로 사용자 조회
        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("⚠️ [LOGIN] Failed. Invalid email: {}", loginDTO.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.error("⚠️ [LOGIN] Failed. Password mismatch for email: {}", loginDTO.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        logger.debug("🎟️ Access Token generated: {}", accessToken);

        // Refresh Token 생성 및 저장
        String refreshTokenValue = saveOrUpdateRefreshToken(user);
        logger.debug("🔑 Refresh Token generated: {}", refreshTokenValue);

        logger.info("✅ [LOGIN] Successful for email: {}", loginDTO.getEmail());
        return Map.of("accessToken", accessToken, "refreshToken", refreshTokenValue);
    }

    private String saveOrUpdateRefreshToken(User user) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserId(user.getId());
        String refreshTokenValue;

        if (existingTokenOpt.isPresent() && existingTokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            refreshTokenValue = existingTokenOpt.get().getToken();
            logger.debug("♻️ [REFRESH TOKEN] Existing token found for user: {}", user.getEmail());
        } else {
            refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());
            logger.debug("🔑 [REFRESH TOKEN] New token generated: {}", refreshTokenValue);

            refreshTokenRepo.save(RefreshToken.builder()
                    .token(refreshTokenValue)
                    .user(user)
                    .deviceId("Default_Device")
                    .expiryDate(LocalDateTime.now().plusWeeks(1))
                    .createdAt(LocalDateTime.now())
                    .build());
            logger.info("✅ [REFRESH TOKEN] Saved to DB for user: {}", user.getEmail());
        }

        return refreshTokenValue;
    }
}
