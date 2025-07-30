package com.capstone1.findable.oauth.service;

import com.capstone1.findable.User.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 및 일반 인증 시 사용되는 Principal 객체
 */
@Builder
@Getter
@Setter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    /** OAuth2 사용자 ID 반환 */
    public Long getId() {
        return user.getId();
    }

    /** 회원가입 여부 반환 */
    public boolean isRegistered() {
        return user.isRegistered();
    }

    /** 권한 설정 (ROLE_ prefix 포함) */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    /** 패스워드 반환 */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /** Spring Security 인증 식별자로 username 사용 */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /** 계정 만료 여부 */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** 계정 잠김 여부 */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 자격증명 만료 여부 */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 계정 활성화 여부 */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /** OAuth2 사용자 속성 반환 */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /** OAuth2 사용자 이름 반환 (기본 username) */
    @Override
    public String getName() {
        return user.getUsername();
    }
}
