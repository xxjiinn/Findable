package com.capstone1.findable.Config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    // 시크릿 키를 외부 설정 파일에서 불러옴 (application.properties or application.yml)
    @Value("${jwt.secret-key}")
    private String secretKey;

    // 액세스 토큰 유효 기간 (1시간)
    private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60;

    // 리프레시 토큰 유효 기간 (1주일)
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;

    // 액세스 토큰 생성 메서드
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 토큰에 사용자 정보 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY)) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS512, secretKey) // 시크릿 키를 사용하여 서명
                .compact(); // 토큰 생성
    }

    // 리프레시 토큰 생성 메서드
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 토큰을 서명할 때 사용한 동일한 시크릿 키 사용
                .build()
                .parseClaimsJws(token) // JWT 토큰 파싱
                .getBody(); // 파싱된 토큰에서 Claims 정보 추출
        return claims.getSubject(); // 사용자 이름 반환
    }

    // 토큰의 유효성을 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 키를 사용하여 토큰을 파싱하고 유효성 확인
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰인 경우 true 반환
        } catch (Exception e) {
            System.out.println("⚠️⚠️Invalid JWT Token: " + e.getMessage()); // 유효하지 않은 경우 로그 출력
            return false; // 유효하지 않은 토큰인 경우 false 반환
        }
    }

    // SecurityContext에 인증 정보 설정
    public static void setAuthentication(UserDetails userDetails) {
        // Spring Security의 UsernamePasswordAuthenticationToken 사용
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities() // 사용자 정보와 권한 설정
        );
        SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 정보 저장
    }
}
