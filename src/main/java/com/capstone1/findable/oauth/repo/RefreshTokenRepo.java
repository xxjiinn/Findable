package com.capstone1.findable.oauth.repo;

import com.capstone1.findable.oauth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token); // 리프레시 토큰으로 엔티티를 찾는 메서드

    Optional<RefreshToken> findByUserId(Long userId); // 유저 ID로 리프레시 토큰 찾기

    @Transactional
    int deleteByExpiryDateBefore(LocalDateTime now);
}