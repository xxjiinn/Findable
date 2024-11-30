package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Builder
@Getter
@Setter
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public Long getId() {
        return user.getId(); // 사용자 ID 반환
    }

    public boolean isRegistered() {
        return user.isRegistered(); // 회원가입 여부 반환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()) // 사용자 역할 기반 권한
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 상태
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2 사용자 정보
    }

    @Override
    public String getName() {
        return user.getUsername(); // OAuth2 사용자 이름
    }
}
