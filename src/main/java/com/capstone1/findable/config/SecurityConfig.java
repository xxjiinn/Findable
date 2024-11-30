package com.capstone1.findable.config;

import com.capstone1.findable.jwt.JwtAuthenticationFilter;
import com.capstone1.findable.oauth.service.PrincipalOauth2UserService;

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
@EnableWebSecurity //security 지원 활성화
@EnableMethodSecurity(securedEnabled = true) //controller 위에 secured 어노테이션 사용 가능하게 만듦
@RequiredArgsConstructor
public class SecurityConfig{

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);

        // 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/user/signup", "/api/user/login").permitAll()
                .requestMatchers("/api/protected-endpoint").authenticated()
                .requestMatchers("/user/**").hasRole("USER")  // 일반 유저 접근 허용
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")  // 매니저와 관리자 접근 허용
                .requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자만 접근 허용
                .requestMatchers("/signup.html", "/login.html").permitAll()  // 회원가입, 로그인 페이지는 누구나 접근 가능
                .anyRequest().authenticated()  // 나머지 모든 요청은 로그인 필요
        );


        // 로그인 및 OAuth2 설정
        http.formLogin(form -> form
                .loginPage("/login.html")  // 로그인 페이지 경로
                .loginProcessingUrl("/login")  // 로그인 처리 경로
                .defaultSuccessUrl("/home.html", true)  // 로그인 성공 후 이동 경로
                .permitAll()
        ).oauth2Login(oauth -> oauth
                .loginPage("/login.html")  // OAuth 로그인 페이지 경로
                .successHandler(customAuthenticationSuccessHandler)  // OAuth2 로그인 성공 후 핸들러 적용
                .defaultSuccessUrl("/home.html", true)  // 로그인 성공 후 이동 경로
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(principalOauth2UserService))  // 사용자 정보 설정
        );

        // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}