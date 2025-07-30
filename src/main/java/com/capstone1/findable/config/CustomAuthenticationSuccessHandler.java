package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.oauth.service.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/** OAuth2 로그인 성공 시 JWT 토큰 생성 및 쿠키에 저장 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    /** 인증 성공 시 호출 */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        PrincipalDetails principal;
        Long userId;
        String subjectEmail;

        // 1) 인증 유형에 따른 사용자 조회
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            String providerId = oauthUser.getAttribute("sub");
            String email = oauthUser.getAttribute("email");
            String username = provider + "_" + providerId;

            // 사용자 조회 또는 자동 등록
            User user = userRepo.findByUsername(username);
            if (user == null) {
                user = userRepo.findByEmail(email).orElse(null);
            }
            if (user == null) {
                user = User.builder()
                        .username(username)
                        .email(email)
                        .name(oauthUser.getAttribute("name"))
                        .password(UUID.randomUUID().toString())
                        .role(Role.ROLE_USER)
                        .provider(provider)
                        .providerId(providerId)
                        .registered(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepo.save(user);
                log.info("🔑 OAuth2 user auto-registered: {}", username);
            }
            principal = PrincipalDetails.builder()
                    .user(user)
                    .attributes(oauthUser.getAttributes())
                    .build();
            userId = user.getId();
            subjectEmail = user.getEmail();

            // SecurityContext 업데이트
            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    principal,
                    principal.getAuthorities(),
                    oauthToken.getAuthorizedClientRegistrationId()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } else {
            // 일반 로그인 처리
            principal = (PrincipalDetails) authentication.getPrincipal();
            userId = principal.getId();
            subjectEmail = principal.getUser().getEmail();
        }

        // 2) 기존 쿠키 제거
        clearCookie(response, "accessToken");
        clearCookie(response, "refreshToken");

        // 3) 토큰 생성 및 관리
        String accessToken = jwtTokenProvider.generateAccessToken(subjectEmail, userId);

        // Refresh Token 관리: 유효 토큰 재사용 혹은 신규 생성
        RefreshToken existing = refreshTokenRepo.findByUserId(userId).orElse(null);
        String refreshToken;
        LocalDateTime expiryDate;
        if (existing != null && existing.getExpiryDate().isAfter(LocalDateTime.now())) {
            refreshToken = existing.getToken();
            expiryDate = existing.getExpiryDate();
        } else {
            refreshToken = jwtTokenProvider.generateRefreshToken(subjectEmail);
            expiryDate = LocalDateTime.now().plusSeconds(refreshTokenValidity / 1000);
            if (existing != null) {
                existing.setToken(refreshToken);
                existing.setExpiryDate(expiryDate);
                existing.setUpdatedAt(LocalDateTime.now());
                refreshTokenRepo.save(existing);
            } else {
                refreshTokenRepo.save(
                        RefreshToken.builder()
                                .user(principal.getUser())
                                .token(refreshToken)
                                .deviceId("oauth2")
                                .expiryDate(expiryDate)
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }
        }

        // 4) SameSite 설정
        String sameSite = cookieSecure ? "None" : "Lax";

        // 5) 쿠키 설정
        addCookie(response, "accessToken", accessToken, true, accessTokenValidity / 1000, sameSite);
        addCookie(response, "refreshToken", refreshToken, true, refreshTokenValidity / 1000, sameSite);

        // 6) 리다이렉트
        response.setHeader("Location", "/home.html");
        response.setStatus(HttpServletResponse.SC_FOUND);
    }

    /** 특정 이름의 쿠키를 삭제 설정 */
    private void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSecure ? "None" : "Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /** 쿠키 생성 유틸 */
    private void addCookie(HttpServletResponse response,
                           String name,
                           String value,
                           boolean httpOnly,
                           long maxAge,
                           String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
