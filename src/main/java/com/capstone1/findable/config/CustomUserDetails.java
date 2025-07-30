package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security UserDetails 구현체
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    /** 회원 ID 반환 */
    public Long getId() {
        return user.getId();
    }

    /** 회원 이메일 반환 */
    public String getEmail() {
        return user.getEmail();
    }

    /** 회원 이름(Username 필드) 반환 */
    public String getName() {
        return user.getUsername();
    }

    /** Spring Security 권한 설정 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    /** 패스워드 반환 */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /** 인증 식별자 반환(email 사용) */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /** 계정 만료 여부(false면 만료) */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** 계정 잠김 여부(false면 잠김) */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 자격증명 만료 여부(false면 만료) */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 계정 활성화 여부(true면 활성화) */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
