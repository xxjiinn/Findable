package com.capstone1.findable.User.service;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("⚠️ 로그인 실패: 사용자 {}를 찾을 수 없습니다.", email);
                    return new UsernameNotFoundException("User not found");
                });

        logger.info("🔍 로그인 시도: {}", email);
        logger.info("✅ 로그인 성공: {}", email);  // 로그인 성공 로그 추가

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())  // 암호화된 비밀번호를 그대로 전달
                .roles("USER")  // 필요에 따라 ROLE 추가
                .build();
    }

}
