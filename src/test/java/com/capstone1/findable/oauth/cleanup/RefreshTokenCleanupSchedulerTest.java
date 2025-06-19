package com.capstone1.findable.oauth.cleanup;

import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class RefreshTokenCleanupSchedulerTest {

    private RefreshTokenRepo repo;
    private RefreshTokenCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        repo = mock(RefreshTokenRepo.class);
        scheduler = new RefreshTokenCleanupScheduler(repo);
    }

    @Test
    void cleanupExpiredTokens_callsRepositoryWithNow() {
        when(repo.deleteByExpiryDateBefore(any(LocalDateTime.class))).thenReturn(5);

        scheduler.cleanupExpiredTokens();

        verify(repo, times(1)).deleteByExpiryDateBefore(any(LocalDateTime.class));
    }
}