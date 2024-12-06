package com.capstone1.findable.jwt;

import com.capstone1.findable.config.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String accessToken = extractTokenFromHeader(request);

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                authenticateUser(accessToken, request);
            } else {
                String refreshToken = extractTokenFromCookies(request, "refreshToken");
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    Claims refreshTokenClaims = jwtTokenProvider.getClaimsFromToken(refreshToken);
                    String username = refreshTokenClaims.getSubject();
                    Long userId = refreshTokenClaims.get("userId", Long.class); // userId 추출

                    // Access Token 생성 시 username과 userId 전달
                    String newAccessToken = jwtTokenProvider.generateAccessToken(username, userId);

                    response.setHeader("Authorization", "Bearer " + newAccessToken); // Access Token 전달
                    authenticateUser(newAccessToken, request);
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error("Authentication error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } catch (Exception e) {
            logger.error("Unexpected error during authentication: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }

        chain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void authenticateUser(String token, HttpServletRequest request) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String username = claims.getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
