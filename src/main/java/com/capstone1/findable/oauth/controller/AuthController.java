package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.Exception.ResourceNotFoundException;
import com.capstone1.findable.Exception.UnauthorizedAccessException;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.BlacklistedToken;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.oauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    /** 로그인 처리 */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody UserDTO.LoginUserDTO loginDTO,
            HttpServletResponse response
    ) {
        log.info("☑️ [LOGIN] email={}", loginDTO.getEmail());
        Map<String, String> tokens = authService.login(loginDTO);
        String accessToken  = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        // 1) 기존에 남아있을지 모를 토큰 쿠키 삭제
        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", clearAccess.toString());
        response.addHeader("Set-Cookie", clearRefresh.toString());

        // 2) SameSite 값을 환경에 맞춰 분기 처리 (HTTP 개발: Lax / HTTPS 운영: None)
        String sameSiteValue = cookieSecure ? "None" : "Lax";

        // 3) 새 Access Token 쿠키 설정
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(accessTokenValidity / 1000)
                .sameSite(sameSiteValue)
                .build();

        // 4) 새 Refresh Token 쿠키 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshTokenValidity / 1000)
                .sameSite(sameSiteValue)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        log.info("✅ [LOGIN] Successful email={}", loginDTO.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId",  String.valueOf(authService.getUserId(loginDTO.getEmail()))
        ));
    }

    /** Access Token 재발급 */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("🔄 [REFRESH] token={}", refreshToken);

        if (blacklistedTokenRepo.findByToken(refreshToken).isPresent()) {
            log.warn("❌ [REFRESH] Blacklisted token");
            throw new UnauthorizedAccessException("Refresh token is blacklisted");
        }

        RefreshToken stored = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("❌ [REFRESH] Token expired");
            throw new UnauthorizedAccessException("Refresh token expired");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Long userId     = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(username, userId);

        // SameSite 분기
        String sameSiteValue = cookieSecure ? "None" : "Lax";

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(accessTokenValidity / 1000)
                .sameSite(sameSiteValue)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());

        log.info("✅ [REFRESH] Issued new accessToken for userId={}", userId);
        return ResponseEntity.ok(Map.of("message", "Token refreshed"));
    }

    /** 로그아웃 처리 */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("🚪 [LOGOUT] token={}", refreshToken);

        // Refresh Token 삭제 및 블랙리스트 등록
        refreshTokenRepo.findByToken(refreshToken).ifPresent(refreshTokenRepo::delete);
        blacklistedTokenRepo.save(BlacklistedToken.builder()
                .token(refreshToken)
                .blacklistedAt(LocalDateTime.now())
                .build()
        );

        // SameSite 분기
        String sameSiteValue = cookieSecure ? "None" : "Lax";

        // 쿠키 만료 설정
        ResponseCookie expiredRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSiteValue)
                .build();
        ResponseCookie expiredAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSiteValue)
                .build();
        response.addHeader("Set-Cookie", expiredRefresh.toString());
        response.addHeader("Set-Cookie", expiredAccess.toString());

        log.info("✅ [LOGOUT] Completed");
        return ResponseEntity.noContent().build();
    }
}
