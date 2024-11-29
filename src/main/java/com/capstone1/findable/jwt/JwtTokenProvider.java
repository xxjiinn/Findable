package com.capstone1.findable.jwt;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepo userRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    // SecretKey 객체 생성 (Keys 사용)
    private SecretKey secretKey;

    // 시크릿 키 초기화
    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    // 액세스 토큰 생성 메서드
    public String generateAccessToken(String username) {
        // 액세스 토큰 유효 기간 (1시간)
        long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60;
        return Jwts.builder()
                .setSubject(username) // 토큰에 사용자 정보 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY)) // 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS512) // SecretKey와 알고리즘을 사용하여 서명
                .compact(); // 토큰 생성
    }

    // 리프레시 토큰 생성 메서드
    public String generateRefreshToken(String username) {
        // 리프레시 토큰 유효 기간 (1주일)
        long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(secretKey, SignatureAlgorithm.HS512) // SecretKey와 알고리즘을 사용하여 서명
                .compact();
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // SecretKey 객체를 사용
                .build()
                .parseClaimsJws(token) // JWT 토큰 파싱
                .getBody(); // 파싱된 토큰에서 Claims 정보 추출
        return claims.getSubject(); // 사용자 이름 반환
    }

    public User getUserFromToken(String token) {
        String username = getUsernameFromToken(token);
        return userRepo.findByUsername(username); // username을 기반으로 사용자 정보 조회
    }

    // 토큰의 유효성을 검증하는 메서드

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("⚠️ Invalid JWT: " + e.getMessage());
            return false;
        }
    }
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey) // SecretKey 객체를 사용
//                    .build()
//                    .parseClaimsJws(token);
//            return true; // 유효한 토큰인 경우 true 반환
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { // 토큰의 서명이 손상되었거나 잘못된 형식인 경우 발생.
//            System.out.println("⚠️ Invalid JWT signature: " + e.getMessage());
//        } catch (ExpiredJwtException e) { // 토큰의 유효 기간이 만료된 경우 발생. -> 클라이언트에게 새로 로그인하거나 리프레시 토큰을 사용해 새로운 Access Token 을 요청하도록 함.
//            System.out.println("⚠️ Expired JWT token: " + e.getMessage());
//        } catch (UnsupportedJwtException e) { // 서버가 지원하지 않는 형식의 JWT를 받았을 때 발생.
//            System.out.println("⚠️ Unsupported JWT token: " + e.getMessage());
//        } catch (IllegalArgumentException e) { // JWT가 비어 있거나 유효하지 않은 문자열일 때 발생.
//            System.out.println("⚠️ JWT token compact of handler are invalid: " + e.getMessage());
//        }
//        return false;
//    }

    // SecurityContext 에 인증 정보 설정
    public static void setAuthentication(UserDetails userDetails) {
        // Spring Security 의 UsernamePasswordAuthenticationToken 사용
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities() // 사용자 정보와 권한 설정
        );
        SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext 에 인증 정보 저장
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepo.findByToken(token).isPresent();
    }
}
