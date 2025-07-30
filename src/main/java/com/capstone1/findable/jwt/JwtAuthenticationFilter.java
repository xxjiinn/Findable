package com.capstone1.findable.jwt;

import com.capstone1.findable.Exception.UnauthorizedAccessException;
import com.capstone1.findable.config.CustomUserDetailsService;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** JWT 기반 인증 필터 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    /** 필터 제외 대상 설정 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // static 리소스, 로그인 페이지, OAuth 엔드포인트만 예외
        // 로그인/리프레시/로그아웃 API
        return path.startsWith("/login.html") ||
                path.startsWith("/signup.html") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/.well-known/") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/login/oauth2/") ||
                path.startsWith("/api/auth/");
    }

    /** JWT 토큰 검증 및 재발급 처리 */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1) Access Token 확인
            String accessToken = resolveToken(request, "accessToken");
            boolean accessValid = false;
            if (accessToken != null) {
                try {
                    accessValid = jwtTokenProvider.validateToken(accessToken);
                    if (accessValid) {
                        setAuthentication(accessToken);
                    }
                } catch (ExpiredJwtException eje) {
                    log.info("☑️ Access token expired, will attempt refresh");
                }
            }

            // 2) Access invalid 시 Refresh Token으로 재발급
            if (!accessValid) {
                String refreshToken = resolveToken(request, "refreshToken");
                if (refreshToken != null
                        && blacklistedTokenRepo.findByToken(refreshToken).isEmpty()
                        && jwtTokenProvider.validateToken(refreshToken)) {

                    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
                    String newAccessToken = jwtTokenProvider.generateAccessToken(username, userId);

                    // 헤더 및 쿠키 설정
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    String sameSiteValue = cookieSecure ? "None" : "Lax";
                    ResponseCookie cookie = ResponseCookie.from("accessToken", newAccessToken)
                            .httpOnly(true)
                            .secure(cookieSecure)
                            .path("/")
                            .maxAge(accessTokenValidity / 1000)
                            .sameSite(sameSiteValue)
                            .build();
                    response.addHeader("Set-Cookie", cookie.toString());

                    setAuthentication(newAccessToken);
                } else {
                    // 모두 invalid 시 쿠키 제거
                    clearCookie(response, "accessToken");
                    clearCookie(response, "refreshToken");
                }
            }
        } catch (UnauthorizedAccessException | JwtException ex) {
            SecurityContextHolder.clearContext();
            log.error("Authentication failed: {}", ex.getMessage());
            clearCookie(response, "accessToken");
            clearCookie(response, "refreshToken");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /** 쿠키 또는 Authorization 헤더에서 토큰 추출 */
    private String resolveToken(HttpServletRequest request, String name) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (name.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    /** SecurityContext에 인증 정보 설정 */
    private void setAuthentication(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /** 특정 이름의 쿠키를 만료시켜 삭제 */
    private void clearCookie(HttpServletResponse response, String name) {
        String sameSiteValue = cookieSecure ? "None" : "Lax";
        ResponseCookie expired = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSiteValue)
                .build();
        response.addHeader("Set-Cookie", expired.toString());
    }
}
