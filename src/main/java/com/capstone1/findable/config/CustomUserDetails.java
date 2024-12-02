package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();  // User 엔티티의 ID 반환
    }

    public String getEmail() {
        return user.getEmail(); // User 엔티티의 이메일 반환
    }

    public boolean isRegistered() {
        return user.isRegistered();  // User 엔티티에 등록 여부를 나타내는 필드
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name())); // Role 기반 권한 설정
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Spring Security는 username 대신 이메일로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 비활성화 계정 관리 필요시 수정 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠긴 계정 관리 필요시 수정 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
