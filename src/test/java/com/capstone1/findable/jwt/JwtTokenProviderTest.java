//package com.capstone1.findable.jwt;
//
//import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
//import io.jsonwebtoken.Claims;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class JwtTokenProviderTest {
//
//    @Value("${jwt.secret-key}")
//    private String secretKey;
//
//    private JwtTokenProvider jwtTokenProvider;
//
//    private BlacklistedTokenRepo blacklistedTokenRepo;
//
//    @BeforeEach
//    void setUp() {
//        // BlacklistedTokenRepo mock 객체 생성
//        blacklistedTokenRepo = Mockito.mock(BlacklistedTokenRepo.class);
//        jwtTokenProvider = new JwtTokenProvider(blacklistedTokenRepo);
//        jwtTokenProvider.setSecretKey(secretKey); // secret key 설정
//    }
//
//    @Test
//    void testGenerateAndValidateAccessToken() {
//        // Given: 테스트용 사용자 이름
//        String username = "testuser";
//
//        // When: Access Token 생성 및 검증
//        String token = jwtTokenProvider.generateAccessToken(username);
//
//        boolean isValid = jwtTokenProvider.validateToken(token);
//        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
//
//        // Then: 토큰이 유효하고 사용자 이름이 일치하는지 확인
//        assertThat(isValid).isTrue();
//        assertThat(extractedUsername).isEqualTo(username);
//    }
//
//    @Test
//    void testGenerateAndValidateRefreshToken() {
//        // Given: 테스트용 사용자 이름
//        String username = "testuser";
//
//        // When: Refresh Token 생성 및 검증
//        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
//
//        boolean isValid = jwtTokenProvider.validateToken(refreshToken);
//        String extractedUsername = jwtTokenProvider.getUsernameFromToken(refreshToken);
//
//        // Then: 토큰이 유효하고 사용자 이름이 일치하는지 확인
//        assertThat(isValid).isTrue();
//        assertThat(extractedUsername).isEqualTo(username);
//    }
//
//    @Test
//    void testExpiredTokenValidation() {
//        // Given: 만료된 토큰 생성
//        String expiredToken = jwtTokenProvider.generateAccessToken("testuser");
//
//        Claims claims = jwtTokenProvider.getClaimsFromToken(expiredToken);
//        claims.setExpiration(new java.util.Date(System.currentTimeMillis() - 1000)); // 1초 전 만료
//
//        // When: 만료된 토큰 검증
//        boolean isValid = jwtTokenProvider.validateToken(expiredToken);
//
//        // Then: 만료된 토큰이 유효하지 않은지 확인
//        assertThat(isValid).isFalse();
//    }
//}
