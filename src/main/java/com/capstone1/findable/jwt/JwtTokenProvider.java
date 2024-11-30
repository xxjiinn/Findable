package com.capstone1.findable.jwt;

import com.capstone1.findable.User.service.UserService;
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

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final BlacklistedTokenRepo blacklistedTokenRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private SecretKey secretKey;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    // Access Token 생성
    public String generateAccessToken(String username) {
        long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 생성
    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        try {
            long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 1주일
            String refreshToken = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
            logger.debug("Generated Refresh Token: {}", refreshToken);
            return refreshToken;
        } catch (Exception e) {
            logger.error("❌ Error generating Refresh Token for username: {}", username, e);
            throw new RuntimeException("Failed to generate Refresh Token");
        }
    }


    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 토큰의 유효성을 검증
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !isTokenExpired(claims) && !isTokenBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepo.findByToken(token).isPresent();
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // JWT 토큰에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
