package com.capstone1.findable.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private SecretKey secretKey;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    // Access Token 생성 (userId 포함)
    public String generateAccessToken(String username, Long userId) {
        return generateToken(username, userId, accessTokenValidity);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return generateToken(username, null, refreshTokenValidity); // userId는 포함하지 않음
    }

    // 토큰 생성 공통 로직
    private String generateToken(String username, Long userId, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("userId", userId) // userId를 클레임에 추가
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims.getExpiration().before(new Date())) {
                logger.warn("Token expired: {}", token);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            logger.error("Failed to extract userId from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // Claims 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
