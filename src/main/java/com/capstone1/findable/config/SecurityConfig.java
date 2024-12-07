package com.capstone1.findable.config;

import com.capstone1.findable.jwt.JwtAuthenticationFilter;
import com.capstone1.findable.jwt.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/user/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/signup.html",
                                "/login.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/",
                                "/oauth2/**" // OAuth2 경로 허용
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/home.html", true) // 로그인 성공 후 이동 페이지
                )
                // 세션 관리 설정 제거 (Spring Security 6.1 이후)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }
}
