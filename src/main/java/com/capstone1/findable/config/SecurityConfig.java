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
        http.csrf(csrf -> csrf.disable()); // CSRF 비활성화 (Authorization 헤더 사용)

        // 인증 및 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/user/signup",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/signup.html",
                        "/login.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/"
                ).permitAll() // 인증 불필요 경로
                .anyRequest().authenticated() // 나머지 요청 인증 필요
        );

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 예외 처리 설정
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized: Invalid or missing tokens.");
                })
        );

        return http.build();
    }
}
