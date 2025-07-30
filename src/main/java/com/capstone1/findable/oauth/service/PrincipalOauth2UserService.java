package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OAuth2 사용자 정보 로드 서비스
 */
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(PrincipalOauth2UserService.class);

    private final UserRepo userRepo;

    /** OAuth2 인증 후 사용자 정보 처리 */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId();
        String providerId = oauthUser.getAttribute("sub");
        String username = provider + "_" + providerId;
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        if (name == null) {
            String given = oauthUser.getAttribute("given_name");
            String family = oauthUser.getAttribute("family_name");
            name = (given != null ? given : "") + " " + (family != null ? family : "");
        }

        // 사용자 엔티티 생성 또는 업데이트
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
            log.info("🔑 New OAuth2 user registered: {}", username);
        } else {
            log.info("🔄 Existing OAuth2 user login: {}", username);
        }

        return new PrincipalDetails(user, oauthUser.getAttributes());
    }
}
