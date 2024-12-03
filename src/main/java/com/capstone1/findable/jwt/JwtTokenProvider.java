package com.capstone1.findable.jwt;

import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    private SecretKey secretKey;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity; // Access Token 유효 시간 (ms)

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity; // Refresh Token 유효 시간 (ms)

    // Access Token 생성
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenValidity);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenValidity);
    }

    // 공통 토큰 생성 메서드
    private String generateToken(String username, long validity) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + validity);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            logger.error("❌ Error generating token for username: {}", username, e);
            throw new RuntimeException("Failed to generate token");
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            if (StringUtils.hasText(token) && token.split("\\.").length == 3) { // 기본 구조 검사
                Claims claims = getClaimsFromToken(token);
                return !isTokenExpired(token) && !isTokenBlacklisted(token);
            } else {
                logger.warn("Token is not in the expected JWT format: {}", token);
                return false;
            }
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    // 토큰 만료 여부 확인 (Public 메서드로 수정)
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = blacklistedTokenRepo.findByToken(token).isPresent();
        if (isBlacklisted) {
            logger.info("Token is blacklisted: {}", token);
        }
        return isBlacklisted;
    }

    // JWT 토큰에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Error parsing token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }
}
