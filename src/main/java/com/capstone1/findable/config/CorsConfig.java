package com.capstone1.findable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${spring.web.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);                // 허용할 출처 목록 설정
        config.setAllowCredentials(true);                        // 클라이언트에서 인증 정보(쿠키, 인증 헤더 등)를 포함한 요청을 허용하겠다.
        config.addAllowedHeader("*");                            // 모든 header 에 응답하는 것을 허용하겠다
        config.addAllowedMethod("*");                            //모든 HTTP 메서드 (post, get, patch, delete 등) 요청을 허용하겠다.
        config.addExposedHeader("Authorization");                // 클라이언트에서 읽을 수 있도록 Authorization 노출
        config.setMaxAge(3600L);                                 // preflight 캐시 유효시간 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  //api로 들어오는 모든 요청은 이 config를 따르겠다 // 모든 경로에 CORS 설정 적용
        return new CorsFilter(source);

    }

}