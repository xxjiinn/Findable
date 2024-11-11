package com.capstone1.findable.Config.filter;

import com.capstone1.findable.Config.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 Authorization 값 추출 (예: "Bearer <token>")
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) { // header가 Bearer로 시작하는지 확인.
            String token = header.substring(7);
            // 토큰 유효성 검사
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);

                // 사용자 정보 조회 후 SecurityContext에 저장.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                JwtTokenProvider.setAuthentication(userDetails); // 인증 설정
            }
        }
        filterChain.doFilter(request, response);
    }
}
