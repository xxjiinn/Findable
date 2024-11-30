package com.capstone1.findable.oauth.repo;

import com.capstone1.findable.oauth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token); // 기존 메서드
    Optional<RefreshToken> findByTokenAndDeviceId(String token, String deviceId); // 새로 추가된 메서드
    Optional<RefreshToken> findByUserId(Long userId);

    @Transactional
    int deleteByExpiryDateBefore(LocalDateTime now);
}
