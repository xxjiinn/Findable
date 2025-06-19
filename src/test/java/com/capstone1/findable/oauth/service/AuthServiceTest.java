//package com.capstone1.findable.oauth.service;
//
//import com.capstone1.findable.User.dto.UserDTO;
//import com.capstone1.findable.User.entity.User;
//import com.capstone1.findable.User.repo.UserRepo;
//import com.capstone1.findable.jwt.JwtTokenProvider;
//import com.capstone1.findable.oauth.entity.RefreshToken;
//import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthServiceTest {
//
//    private UserRepo userRepo;
//    private BCryptPasswordEncoder encoder;
//    private JwtTokenProvider jwtProvider;
//    private RefreshTokenRepo refreshRepo;
//    private AuthService authService;
//
//    @BeforeEach
//    void setUp() {
//        userRepo = mock(UserRepo.class);
//        encoder = new BCryptPasswordEncoder();
//        jwtProvider = mock(JwtTokenProvider.class);
//        refreshRepo = mock(RefreshTokenRepo.class);
//        authService = new AuthService(userRepo, encoder, jwtProvider, refreshRepo);
//    }
//
//    @Test
//    void login_success_createsAndReturnsTokens() {
//        String rawPwd = "secret";
//        String hashed = encoder.encode(rawPwd);
//        User user = User.builder().id(7L).email("u@e.com").password(hashed).build();
//        when(userRepo.findByEmail("u@e.com")).thenReturn(Optional.of(user));
//        when(jwtProvider.generateAccessToken("u@e.com", 7L)).thenReturn("ACCESS");
//        when(jwtProvider.generateRefreshToken("u@e.com")).thenReturn("REFRESH");
//        when(refreshRepo.findByUserId(7L)).thenReturn(Optional.empty());
//
//        Map<String, String> tokens = authService.login(new UserDTO.LoginUserDTO("u@e.com", rawPwd));
//
//        assertThat(tokens).containsEntry("accessToken", "ACCESS")
//                .containsEntry("refreshToken", "REFRESH");
//        verify(refreshRepo, times(1)).save(any(RefreshToken.class));
//    }
//
//    @Test
//    void login_wrongPassword_throwsException() {
//        User user = User.builder().email("u@e.com").password(encoder.encode("abc")).build();
//        when(userRepo.findByEmail("u@e.com")).thenReturn(Optional.of(user));
//
//        assertThatThrownBy(() ->
//                authService.login(new UserDTO.LoginUserDTO("u@e.com", "bad"))
//        ).isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Invalid email or password");
//    }
//}