package com.capstone1.findable.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays; //
import java.util.List;


@Configuration
public class CorsConfig {
    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter(){
        // org.springframework.web.cors.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //frontEnd에서 axios로 처리 가능하게 만들겠다
//        config.addAllowedOrigin("*"); //모든 ip에 응답을 허용하겠다 ////
        config.setAllowedOriginPatterns(List.of("*"));
        config.addAllowedHeader("*"); //모든 header에 응답을 허용하겠다
        config.addAllowedMethod("*"); //모든 post,get,put,delete,patch 요청을 허용하겠다
        source.registerCorsConfiguration("/*", config); //api로 들어오는 모든 요청은 이 config를 따르겠다
        return new CorsFilter(source);

    }

}