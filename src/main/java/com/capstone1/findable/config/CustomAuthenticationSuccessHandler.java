package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo; // 수정된 부분: RefreshTokenRepo 추가

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal(); // 수정된 부분: User 객체로 캐스팅
        String username = user.getUsername();

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(username);

        // Refresh Token 생성 및 저장
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
        refreshTokenRepo.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .deviceId("OAuth2_Login") // 기본 디바이스 ID 설정
                .expiryDate(LocalDateTime.now().plusWeeks(1))
                .createdAt(LocalDateTime.now())
                .build());

        // 토큰을 클라이언트에 전달
        addCookie(response, "accessToken", accessToken, false); // 수정된 부분: Access Token 쿠키에 추가
        addCookie(response, "refreshToken", refreshToken, true); // 수정된 부분: Refresh Token 쿠키에 추가

        // 응답 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful with tokens issued");
    }

    private void addCookie(HttpServletResponse response, String name, String value, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(true); // HTTPS에서만 동작
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일 유효
        response.addCookie(cookie);
    }
}
