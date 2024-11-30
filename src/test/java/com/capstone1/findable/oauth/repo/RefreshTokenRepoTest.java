//package com.capstone1.findable.oauth.repo;
//
//import com.capstone1.findable.User.entity.User;
//import com.capstone1.findable.User.repo.UserRepo;
//import com.capstone1.findable.oauth.entity.RefreshToken;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class RefreshTokenRepoTest {
//
//    @Autowired
//    private RefreshTokenRepo refreshTokenRepo;
//
//    @Autowired
//    private UserRepo userRepo;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 사용자 추가
//        testUser = userRepo.save(User.builder()
//                .username("testuser")
//                .email("testuser@gmail.com")
//                .password("password")
//                .build());
//    }
//
//    @Test
//    void testSaveAndRetrieveRefreshToken() {
//        // Given: 새 RefreshToken 생성
//        String tokenValue = "test-refresh-token";
//        RefreshToken refreshToken = RefreshToken.builder()
//                .user(testUser)
//                .token(tokenValue)
//                .deviceId("TestDevice")
//                .expiryDate(LocalDateTime.now().plusDays(7))
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        refreshTokenRepo.save(refreshToken);
//
//        // When: 저장된 RefreshToken 조회
//        Optional<RefreshToken> retrievedToken = refreshTokenRepo.findByUserId(testUser.getId());
//
//        // Then: RefreshToken이 존재하며, 값이 일치하는지 확인
//        assertThat(retrievedToken).isPresent();
//        assertThat(retrievedToken.get().getToken()).isEqualTo(tokenValue);
//    }
//
//    @Test
//    void testDeleteExpiredTokens() {
//        // Given: 만료된 RefreshToken 생성
//        RefreshToken expiredToken = RefreshToken.builder()
//                .user(testUser)
//                .token("expired-refresh-token")
//                .deviceId("ExpiredDevice")
//                .expiryDate(LocalDateTime.now().minusDays(1)) // 하루 전 만료
//                .createdAt(LocalDateTime.now().minusDays(2))
//                .build();
//
//        refreshTokenRepo.save(expiredToken);
//
//        // When: 만료된 토큰 삭제
//        int deletedCount = refreshTokenRepo.deleteByExpiryDateBefore(LocalDateTime.now());
//
//        // Then: 삭제된 토큰 개수 확인
//        assertThat(deletedCount).isEqualTo(1);
//    }
//}
