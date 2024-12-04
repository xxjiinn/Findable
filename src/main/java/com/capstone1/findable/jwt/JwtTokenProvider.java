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
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    // Access Token 생성
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenValidity);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenValidity);
    }

    private String generateToken(String username, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            boolean isExpired = claims.getExpiration().before(new Date());
            boolean isBlacklisted = isTokenBlacklisted(token);

            if (isExpired) logger.warn("Token expired: {}", token);
            if (isBlacklisted) logger.warn("Token is blacklisted: {}", token);

            return !isExpired && !isBlacklisted;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // 사용자 이름 추출 메서드 추가
    public String getUsernameFromToken(String token) { // 추가된 부분
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // 블랙리스트 확인
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepo.findByToken(token).isPresent();
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
