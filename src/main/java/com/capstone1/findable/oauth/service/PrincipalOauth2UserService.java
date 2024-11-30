package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //userRequest에서는 구글에서 받은 유저 정보가 있다
        System.out.println("getClientRegistration : " + userRequest.getClientRegistration()); //어떤 OAuth로 로그인했는지 확인 가능

        //구글 로그인 버튼 클릭->구글 로그인 창->로그인 후 code리턴받고 이 code를 oauth client라이브러리가 받아서 access token 요청
        //userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필 받아준다
        OAuth2User oauth2User = super.loadUser(userRequest);
        System.out.println("attributes : " + oauth2User.getAttributes());

        //자동 회원가입
        String provider = userRequest.getClientRegistration().getClientId(); //google
        String providerId = oauth2User.getAttribute("sub"); //google의 primary key
        String username = provider + "_" + providerId; //google_sub -> 중복될 일 없음
        String email = oauth2User.getAttribute("email");
        String role = "ROLE_USER";
        String name = oauth2User.getAttribute("name");
        if (name == null) {
            name = oauth2User.getAttribute("given_name") + " " + oauth2User.getAttribute("family_name");
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            user = User.builder()
                    .username(username)
                    .email(email)
                    .name(oauth2User.getAttribute("name")) // name 필드를 Google 응답에서 설정
                    .password(UUID.randomUUID().toString()) // Google OAuth2 로그인 시 비밀번호는 랜덤 UUID로 설정
                    .loginDate(LocalDateTime.now()) // 현재 시간으로 로그인 날짜 설정
                    .role(Role.valueOf(role))
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepo.save(user);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());

        // 유효한 Refresh Token이 있는지 확인
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserId(user.getId());
        String refreshTokenValue;
        if (existingTokenOpt.isPresent() && existingTokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            // 유효한 토큰이 있다면 해당 토큰 재사용
            refreshTokenValue = existingTokenOpt.get().getToken();
        } else {
            // 유효한 토큰이 없다면 새로 발급
            refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getUsername());
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusWeeks(1)) // 1주일 후 만료
                    .createdAt(LocalDateTime.now())
                    .build();
            refreshTokenRepo.save(newRefreshToken);
        }

        System.out.println("🎟️Access Token: " + accessToken);
        System.out.println("🎫Refresh Token: " + refreshTokenValue);

        //oauth login하면 user와 attributes Map을 가지고 authentication을 만들어준다
        return new PrincipalDetails(user, oauth2User.getAttributes()); //PrincipalDetails가 Authentication에 들어간다
    }
}