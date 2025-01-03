package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.service.UserService;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.oauth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO.LoginUserDTO loginDTO, HttpServletResponse response) {
        logger.info("🔥 [LOGIN] Attempt with email: {}", loginDTO.getEmail());
        try {
            // 사용자 정보를 가져오기
            UserDTO.ReadUserDTO user = userService.findByEmail(loginDTO.getEmail());

            // Access Token에 userId 포함하여 생성
            Map<String, String> tokens = authService.login(loginDTO);
            String accessTokenWithUserId = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());

            // Refresh Token 처리
            String refreshToken = tokens.get("refreshToken");

            // 쿠키에 토큰 추가
            addTokenToCookie(response, "accessToken", accessTokenWithUserId, false, false);
            addTokenToCookie(response, "refreshToken", refreshToken, true, true);

            logger.info("✅ [LOGIN] Successful for email: {}", loginDTO.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "accessToken", accessTokenWithUserId,
                    "userId", String.valueOf(user.getId()) // userId 반환
            ));
        } catch (IllegalArgumentException e) {
            logger.error("⚠️ [LOGIN] Failed for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        logger.info("Received refresh token request");

        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                logger.warn("Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            Optional<RefreshToken> storedTokenOpt = refreshTokenRepo.findByToken(refreshToken);
            if (storedTokenOpt.isEmpty() || storedTokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                logger.warn("Stored refresh token not found or expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            // 사용자 정보에서 username과 userId를 추출하여 새로운 Access Token 생성
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            Long userId = userService.findByEmail(username).getId();
            String newAccessToken = jwtTokenProvider.generateAccessToken(username, userId);

            logger.info("New Access Token issued for user: {}", username);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            logger.error("Error during refresh token process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        try {
            Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
            refreshTokenEntity.ifPresent(refreshTokenRepo::delete);

            // 쿠키 삭제
            removeCookie(response, "refreshToken");

            logger.info("User logged out successfully");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void addTokenToCookie(HttpServletResponse response, String name, String token, boolean httpOnly, boolean secure) {
        response.addCookie(new jakarta.servlet.http.Cookie(name, token) {{
            setHttpOnly(httpOnly);
            setSecure(secure);
            setPath("/");
            setMaxAge(7 * 24 * 60 * 60);
        }});
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        response.addCookie(new jakarta.servlet.http.Cookie(cookieName, null) {{
            setHttpOnly(true);
            setSecure(true);
            setPath("/");
            setMaxAge(0);
        }});
    }
}
