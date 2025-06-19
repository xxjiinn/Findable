package com.capstone1.findable.config;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepo userRepo;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userDetailsService = new CustomUserDetailsService(userRepo);
    }

    @Test
    void loadUserByUsername_found() {
        User user = User.builder()
                .id(42L)
                .email("foo@bar.com")
                .password("pwd")
                .username("foo")
                .registered(true)
                .build();
        when(userRepo.findByEmail("foo@bar.com")).thenReturn(Optional.of(user));

        CustomUserDetails details = (CustomUserDetails) userDetailsService.loadUserByUsername("foo@bar.com");

        assertThat(details.getUsername()).isEqualTo("foo@bar.com");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.getId()).isEqualTo(42L);
        verify(userRepo, times(1)).findByEmail("foo@bar.com");
    }

    @Test
    void loadUserByUsername_notFound() {
        when(userRepo.findByEmail("nope@bar.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("nope@bar.com")
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: nope@bar.com");
    }
}