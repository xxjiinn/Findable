package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationSuccessHandlerTest {

    private JwtTokenProvider jwtProvider;
    private RefreshTokenRepo refreshRepo;
    private CustomAuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtTokenProvider.class);
        refreshRepo = mock(RefreshTokenRepo.class);
        handler = new CustomAuthenticationSuccessHandler(jwtProvider, refreshRepo);
    }

    @Test
    void onAuthenticationSuccess_newRefreshTokenSaved_andCookiesSet() throws Exception {
        User user = User.builder().id(3L).username("foo").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(jwtProvider.generateAccessToken("foo", 3L)).thenReturn("AT");
        when(jwtProvider.generateRefreshToken("foo")).thenReturn("RT");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(null, response, auth);

        // RefreshTokenRepo 저장 검증
        verify(refreshRepo, times(1)).save(argThat(rt ->
                rt.getToken().equals("RT") &&
                        rt.getUser().getId() == 3L &&
                        rt.getExpiryDate().isAfter(LocalDateTime.now())
        ));
        // 쿠키 설정 검증
        assertThat(response.getCookies()).anyMatch(c -> c.getName().equals("accessToken") && c.getValue().equals("AT"));
        assertThat(response.getCookies()).anyMatch(c -> c.getName().equals("refreshToken") && c.getValue().equals("RT"));
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void onAuthenticationSuccess_overwritesExistingRefreshToken() throws Exception {
        User user = User.builder().id(5L).username("bar").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(jwtProvider.generateAccessToken("bar", 5L)).thenReturn("NEW_AT");
        when(jwtProvider.generateRefreshToken("bar")).thenReturn("NEW_RT");
        // 기존 토큰이 있더라도 handler 로직은 무조건 새로 발급
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(null, response, auth);

        verify(refreshRepo, times(1)).save(any(RefreshToken.class));
        assertThat(response.getCookies()).anyMatch(c -> c.getName().equals("accessToken") && c.getValue().equals("NEW_AT"));
        assertThat(response.getCookies()).anyMatch(c -> c.getName().equals("refreshToken") && c.getValue().equals("NEW_RT"));
    }
}