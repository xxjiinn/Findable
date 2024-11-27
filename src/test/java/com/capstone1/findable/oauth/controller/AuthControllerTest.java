package com.capstone1.findable.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.dto.RefreshTokenRequest;
import com.capstone1.findable.oauth.entity.BlacklistedToken;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private BlacklistedTokenRepo blacklistedTokenRepo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExample() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 테스트 코드 작성
    }

    @Test
    public void testRefreshAccessToken_Success() {
        // Given
        String validRefreshToken = "valid_refresh_token";
        User mockUser = User.builder()
                .username("test_user")
                .build();

        RefreshToken storedToken = RefreshToken.builder()
                .token(validRefreshToken)
                .user(mockUser)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        when(refreshTokenRepo.findByToken(validRefreshToken)).thenReturn(Optional.of(storedToken));
        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(mockUser.getUsername())).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshToken(mockUser.getUsername())).thenReturn("new_refresh_token");

        // When
        ResponseEntity<?> response = authController.refreshAccessToken(
                new RefreshTokenRequest(validRefreshToken)
        );

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("new_access_token"));
        assertTrue(response.getBody().toString().contains("new_refresh_token"));

        verify(refreshTokenRepo, times(1)).delete(storedToken);
        verify(refreshTokenRepo, times(1)).save(any(RefreshToken.class));
    }

    @Test
    public void testRefreshAccessToken_BlacklistedToken() {
        // Given
        String blacklistedToken = "blacklisted_token";
        when(jwtTokenProvider.isTokenBlacklisted(blacklistedToken)).thenReturn(true);

        // When
        ResponseEntity<?> response = authController.refreshAccessToken(
                new RefreshTokenRequest(blacklistedToken)
        );

        // Then
        assertEquals(401, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Refresh Token is blacklisted. Please log in again."));

        verify(refreshTokenRepo, never()).findByToken(blacklistedToken);
    }


    @Test
    public void testLogout_Success() {
        // Given
        String validRefreshToken = "valid_refresh_token";
        when(refreshTokenRepo.findByToken(validRefreshToken)).thenReturn(Optional.of(
                RefreshToken.builder()
                        .token(validRefreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(1))
                        .build()
        ));

        // When
        ResponseEntity<String> response = authController.logout(new RefreshTokenRequest(validRefreshToken));

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Successfully logged out", response.getBody());

        verify(refreshTokenRepo, times(1)).delete(any(RefreshToken.class));
        verify(blacklistedTokenRepo, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    public void testLogout_InvalidToken() {
        // Given
        String invalidRefreshToken = "invalid_refresh_token";
        when(refreshTokenRepo.findByToken(invalidRefreshToken)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = authController.logout(new RefreshTokenRequest(invalidRefreshToken));

        // Then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid refresh token", response.getBody());

        verify(refreshTokenRepo, never()).delete(any());
        verify(blacklistedTokenRepo, never()).save(any());
    }

}
