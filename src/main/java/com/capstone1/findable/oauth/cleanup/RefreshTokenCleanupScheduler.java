package com.capstone1.findable.oauth.cleanup;

import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleanupScheduler.class);
    private final RefreshTokenRepo refreshTokenRepo;

    // 매일 새벽 2시에 만료된 Refresh Token 삭제
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        logger.info("🔄Running scheduled cleanup for expired Refresh Tokens.");
        int deletedCount = refreshTokenRepo.deleteByExpiryDateBefore(LocalDateTime.now());
        logger.info("✅Cleanup completed. Deleted {} expired tokens.", deletedCount);
    }
}
