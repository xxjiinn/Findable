package com.capstone1.findable.oauth.cleanup;

import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** 만료된 Refresh Token을 주기적으로 청소하는 스케줄러 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepo refreshTokenRepo;

    /** 매일 02:00 (Asia/Seoul) 기준으로 만료된 Refresh Token 삭제 */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("[RefreshTokenCleanup] 시작 - 만료된 Refresh Token 정리...");
        try {
            int deletedCount = refreshTokenRepo.deleteByExpiryDateBefore(LocalDateTime.now());
            log.info("[RefreshTokenCleanup] 완료 - 삭제된 토큰 수: {}", deletedCount);
        } catch (Exception e) {
            log.error("[RefreshTokenCleanup] 예외 발생: {}", e.getMessage(), e);
        }
    }
}
