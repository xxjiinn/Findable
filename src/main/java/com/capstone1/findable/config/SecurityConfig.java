package com.capstone1.findable.config;

import com.capstone1.findable.jwt.JwtAuthenticationFilter;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.service.PrincipalOauth2UserService;
import com.capstone1.findable.config.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // 인증 및 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/user/signup", "/api/user/login").permitAll() // 인증 불필요 API
                .requestMatchers("/signup.html", "/login.html", "/home.html", "/css/**", "/js/**").permitAll() // 정적 리소스 공개
                .requestMatchers("/api/protected-endpoint").authenticated()
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        // Form Login 설정
        http.formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home.html", true) // 로그인 성공 시 리다이렉트 경로
                .permitAll()
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth -> oauth
                .loginPage("/login.html")
                .successHandler(customAuthenticationSuccessHandler)
                .defaultSuccessUrl("/home.html", true)
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(principalOauth2UserService))
        );

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
