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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
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

            if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
                authenticateUser(accessToken, request);
            } else {
                String refreshToken = extractTokenFromCookies(request, "refreshToken");
                if (StringUtils.hasText(refreshToken) && jwtTokenProvider.validateToken(refreshToken)) {
                    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                    String newAccessToken = jwtTokenProvider.generateAccessToken(username);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    authenticateUser(newAccessToken, request);
                }
            }
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
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
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
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
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
