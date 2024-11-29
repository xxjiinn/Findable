package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한은 여기서 처리할 수 있음.
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
