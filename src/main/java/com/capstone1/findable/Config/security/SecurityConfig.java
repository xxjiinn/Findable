package com.capstone1.findable.Config.security;

import com.capstone1.findable.Config.filter.JwtAuthenticationFilter;
import com.capstone1.findable.oauth.service.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (REST API 사용 시 주로 비활성화)
                .cors(withDefaults()) // CORS 설정 활성화

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/signup", "/api/user/login", "/signup.html", "/login.html", "/js/**", "/css/**").permitAll() // 회원가입, 로그인, 정적 리소스 접근 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )

                // 로그인 및 OAuth2 설정
                .formLogin(form -> form
                        .loginPage("/login.html")  // 로그인 페이지 경로
                        .loginProcessingUrl("/api/user/login")  // 로그인 처리 경로
                        .defaultSuccessUrl("/home.html", true)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/home.html", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService))
                );

                // 세션을 사용하지 않고 무상태(stateless) 설정
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}



// http.csrf(AbstractHttpConfigurer::disable); // CSRF 비활성화 (REST API 사용 시 주로 비활성화)
// REST API와 같은 상태를 저장하지 않는 애플리케이션에서 자주 사용하는 설정
// 다른 도메인에서 API호출되는거 막지 않겠다. Rest Api -> 브라우저 통해 request 받아서 꺼도 됨.
//                .cors(AbstractHttpConfigurer::disable); // 이거 disable 안 하면 프론트에서 요청 보냈을 때 response 안 하고 에러 발생시킴
//                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));