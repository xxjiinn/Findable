//package com.capstone1.findable.oauth.controller;
//
//import com.capstone1.findable.User.entity.User;
//import com.capstone1.findable.Config.jwt.JwtTokenProvider;
//import com.capstone1.findable.oauth.dto.RefreshTokenRequest;
//import com.capstone1.findable.oauth.entity.RefreshToken;
//import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final RefreshTokenRepo refreshTokenRepo;
//
//    // 리프레시 토큰으로 새로운 액세스 토큰 발급
//    @PostMapping("/refresh")
//    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
//        String refreshToken = request.getRefreshToken();
//        logger.info("Received Refresh Token: {}", refreshToken);
//
//        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
//
//        if (refreshTokenEntity.isPresent()) {
//            logger.info("Found token in DB: {}", refreshTokenEntity.get().getToken());
//        } else {
//            logger.warn("Token not found in DB");
//        }
//
//        if (refreshTokenEntity.isPresent() && refreshTokenEntity.get().getExpiryDate().isAfter(LocalDateTime.now())) {
//            // JWT 리프레시 토큰 검증
//            if (jwtTokenProvider.validateToken(refreshToken)) {
//                // 리프레시 토큰이 유효하다면
//                User user = refreshTokenEntity.get().getUser();
//                String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
//                logger.info("Generated new Access Token: {}", newAccessToken);
//                return ResponseEntity.ok(newAccessToken);
//            } else {
//                logger.warn("JWT Token validation failed");
//                return ResponseEntity.status(401).body("⚠️Invalid JWT signature or malformed token");
//            }
//        } else {
//            logger.warn("Invalid or expired refresh token");
//            return ResponseEntity.status(401).body("⚠️⚠️Invalid or expired refresh token");
//        }
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
//        String refreshToken = request.getRefreshToken();
//        logger.info("Received Logout Request with Refresh Token: {}", refreshToken);
//
//        // DB에서 해당 리프레시 토큰을 찾는다
//        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken);
//
//        if (refreshTokenEntity.isPresent()) {
//            // 토큰 삭제
//            refreshTokenRepo.delete(refreshTokenEntity.get());
//            logger.info("Refresh token invalidated and removed from DB");
//            return ResponseEntity.ok("Successfully logged out");
//        } else {
//            logger.warn("Token not found for logout");
//            return ResponseEntity.status(400).body("Invalid refresh token");
//        }
//
//        // 만약 리프레시 토큰을 삭제하지 않고 비활성화 상태로 유지하고 싶다면,
//        // 토큰의 expiryDate를 현재 시간으로 설정해 만료시키는 방법!
////        if (refreshTokenEntity.isPresent()) {
////            RefreshToken token = refreshTokenEntity.get();
////            token.setExpiryDate(LocalDateTime.now()); // 만료일을 현재 시간으로 설정
////            refreshTokenRepo.save(token);
////            logger.info("Refresh token invalidated by setting expiry date");
////            return ResponseEntity.ok("Successfully logged out");
////        }
//
//    }
//
//
//}
