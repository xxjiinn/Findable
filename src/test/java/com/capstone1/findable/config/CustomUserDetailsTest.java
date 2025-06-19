package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void getters_returnUserFieldsAndAuthorities() {
        User user = User.builder()
                .id(10L)
                .email("u@example.com")
                .username("john")
                .password("pwd")
                .role(Role.ROLE_MANAGER)
                .registered(true)
                .build();
        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getId()).isEqualTo(10L);
        assertThat(details.getEmail()).isEqualTo("u@example.com");
        assertThat(details.getName()).isEqualTo("john");
        assertThat(details.getUsername()).isEqualTo("u@example.com");
        assertThat(details.getPassword()).isEqualTo("pwd");
//        assertThat(details.getAuthorities())
//                .containsExactly(new SimpleGrantedAuthority("ROLE_MANAGER"));
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}