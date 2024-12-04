package com.capstone1.findable.config;

import com.capstone1.findable.jwt.JwtAuthenticationFilter;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.config.CustomUserDetailsService;
import com.capstone1.findable.oauth.service.PrincipalOauth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService; // 수정된 부분: CustomUserDetailsService 주입

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService); // 수정된 부분: CustomUserDetailsService 추가
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // 인증 및 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/user/signup", "/api/user/login").permitAll() // 인증 불필요 API
                .requestMatchers("/signup.html", "/login.html", "/home.html", "/css/**", "/js/**").permitAll() // 정적 리소스
                .requestMatchers("/api/**").authenticated() // 게시물 관련 API 인증 필요
                .anyRequest().authenticated() // 나머지 요청 인증 필요
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth -> oauth
                .loginPage("/login.html")
                .successHandler(customAuthenticationSuccessHandler) // 성공 핸들러 등록
                .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService))
        );

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
