package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();  // User 엔티티의 ID 반환
    }

    public boolean isRegistered() {
        return user.isRegistered();  // User 엔티티에 등록 여부를 나타내는 필드가 있어야 함
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Spring Security 는 hasRole 사용 시 자동으로 ROLE_을 붙임.
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail() {
        return user.getEmail(); // User 엔티티의 이메일 반환
    }
}
