package com.capstone1.findable.User.service;

import com.capstone1.findable.User.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환하는 메서드입니다. 필요에 따라 설정하세요.
        return Collections.emptyList(); // 예제에서는 빈 리스트 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // User 엔티티에서 이메일을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않음을 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않음을 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않음을 반환
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되었음을 반환
    }
}
