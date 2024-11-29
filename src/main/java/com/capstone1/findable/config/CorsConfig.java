package com.capstone1.findable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;


@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        // org.springframework.web.cors.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //frontEnd에서 axios로 처리 가능하게 만들겠다 // 클라이언트에서 인증 정보(쿠키, 인증 헤더 등)를 포함한 요청을 허용하겠다.
//        config.addAllowedOrigin("*"); //모든 ip에 응답을 허용하겠다
        config.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용
//        config.setAllowedOriginPatterns(Arrays.asList("https://example.com")); // 하지만 보안 강화를 위해 실제 사용하는 도메인만 명시하는 것이 좋음.
        config.addAllowedHeader("*"); //모든 header에 응답을 허용하겠다
        config.addAllowedMethod("*"); //모든 HTTP 메서드 (post, get, patch, delete 등) 요청을 허용하겠다
        source.registerCorsConfiguration("/*", config); //api로 들어오는 모든 요청은 이 config를 따르겠다 // 모든 경로에 대해 설정 적용
        return new CorsFilter(source);

    }

}