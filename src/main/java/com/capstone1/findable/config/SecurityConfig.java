package com.capstone1.findable.config;

import com.capstone1.findable.jwt.JwtAuthenticationFilter;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.repo.BlacklistedTokenRepo;
import com.capstone1.findable.oauth.service.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final com.capstone1.findable.config.CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenRepo blacklistedTokenRepo;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /** JWT 필터 빈 등록 */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, blacklistedTokenRepo);
    }

    /** 보안 필터 체인 설정 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())                         /** CORS 설정 적용 */
                .csrf(AbstractHttpConfigurer::disable)                            /** CSRF 비활성화 */
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) /** 세션 관리 설정 */
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (req, res, authEx) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, authEx.getMessage())
                ))                                                        /** 인증 실패 처리 */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() /** Preflight 요청 허용 */
                        .requestMatchers(
                                "/",
                                "/signup.html",
                                "/login.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/.well-known/**"
                        ).permitAll()                                         /** 정적 리소스 허용 */
                        .requestMatchers(
                                "/api/user/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()                                         /** 인증 없이 접근 허용 */
                        .anyRequest().authenticated()                         /** 나머지 요청 인증 필요 */
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html")                          /** 커스텀 로그인 페이지 */
                        .userInfoEndpoint(ui -> ui
                                .userService(principalOauth2UserService)         /** OAuth2UserService */
                        )
                        .successHandler(customAuthenticationSuccessHandler) /** 로그인 성공 핸들러 */
                        .failureHandler((req, res, ex) -> {                 /** 로그인 실패 처리 */
                            log.error("OAuth2 로그인 실패", ex);
                            res.sendRedirect("/login.html?oauth2failure");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); /** JWT 필터 등록 */

        return http.build();
    }
}
