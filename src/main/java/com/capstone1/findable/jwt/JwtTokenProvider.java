package com.capstone1.findable.jwt;

import com.capstone1.findable.Exception.UnauthorizedAccessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${jwt.secret-key}")
    private String secretKeyString;
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private SecretKey secretKey;
    private JwtParser jwtParser;


    /** 초기화: Base64 디코딩 및 JwtParser 빌드 */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }


    /** Access Token 생성 (username, userId 클레임 포함) */
    public String generateAccessToken(String username, Long userId) {
        return buildToken(username, userId, accessTokenValidity);
    }

    /** Refresh Token 생성 (username만 클레임에 포함) */
    public String generateRefreshToken(String username) {
        return buildToken(username, null, refreshTokenValidity);
    }
    /** 토큰 생성 공통 로직 */
    private String buildToken(String subject, Long userId, long validityMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512);

        if (userId != null) {
            builder.claim("userId", userId);
        }

        return builder.compact();
    }

    /** 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token", ex);
            return false;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token", ex);
            return false;
        }
    }

    /** 토큰에서 username 추출 */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** 토큰에서 userId 클레임 추출 */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    /** Claims 파싱 및 예외 처리: 유효하지 않을 경우 예외 발생 */
    private Claims parseClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token", ex);
            throw new UnauthorizedAccessException("Expired JWT token");
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token", ex);
            throw new UnauthorizedAccessException("Invalid JWT token");
        }
    }
}
