package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.controller.AuthController;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepo refreshTokenRepo;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 사용자 정보 파싱
        String provider = userRequest.getClientRegistration().getClientId();
        String providerId = oauth2User.getAttribute("sub");
        String username = provider + "_" + providerId;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        if (name == null) {
            name = oauth2User.getAttribute("given_name") + " " + oauth2User.getAttribute("family_name");
        }

        // 사용자 동기화
        User user = userRepo.findByUsername(username);
        if (user == null) {
            user = User.builder()
                    .username(username)
                    .email(email)
                    .name(name)
                    .password(UUID.randomUUID().toString())
                    .role(Role.ROLE_USER)
                    .provider(provider)
                    .providerId(providerId)
                    .registered(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepo.save(user);
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getId());

        // Refresh Token 생성 및 저장
        String refreshTokenValue;
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserId(user.getId());
        if (existingTokenOpt.isPresent() && existingTokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            refreshTokenValue = existingTokenOpt.get().getToken();
        } else {
            refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getUsername());
            refreshTokenRepo.save(RefreshToken.builder()
                    .token(refreshTokenValue)
                    .user(user)
                    .deviceId("OAuth2_Login") // OAuth2 로그인을 위한 기본 디바이스 ID
                    .expiryDate(LocalDateTime.now().plusWeeks(1))
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        logger.info("🎟️ Access Token: " + accessToken);
        logger.info("🎫 Refresh Token: " + refreshTokenValue);

        return new PrincipalDetails(user, oauth2User.getAttributes());
    }

}
