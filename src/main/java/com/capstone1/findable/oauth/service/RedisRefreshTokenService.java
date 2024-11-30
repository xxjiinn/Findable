//package com.capstone1.findable.oauth.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class RedisRefreshTokenService {
//
//    private final RedisTemplate<String, String> redisTemplate;
//
//
//     // Refresh Token 저장
//    public void saveRefreshToken(String username, String token) {
//        redisTemplate.opsForValue().set(getKey(username), token, 7, TimeUnit.DAYS);
//    }
//
//     //Refresh Token 조회
//    public String getRefreshToken(String username) {
//        return redisTemplate.opsForValue().get(getKey(username));
//    }
//
//     // Refresh Token 삭제
//    public void deleteRefreshToken(String username) {
//        redisTemplate.delete(getKey(username));
//    }
//
//    // Redis Key 명명 규칙
//    private String getKey(String username) {
//        return "refresh_token:" + username;
//    }
//}
