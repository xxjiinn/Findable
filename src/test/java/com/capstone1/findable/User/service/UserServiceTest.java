package com.capstone1.findable.User.service;

import com.capstone1.findable.Exception.CustomConflictException;
import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepo userRepo;
    private BCryptPasswordEncoder encoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        encoder = new BCryptPasswordEncoder();
        // jwtProvider, refreshRepo는 사용되지 않는 생성자 오버로드 가정
        userService = new UserService(userRepo, encoder, null, null);
    }

    @Test
    void createUser_successfullySavesNewUser() {
        UserDTO.CreateUserDTO dto = UserDTO.CreateUserDTO.builder()
                .name("Alice")
                .email("alice@example.com")
                .password("secret")
                .build();

        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        userService.createUser(dto);

        verify(userRepo).save(argThat((User u) ->
                u.getUsername().equals("Alice") &&
                        u.getEmail().equals("alice@example.com") &&
                        encoder.matches("secret", u.getPassword()) &&
                        u.getRole() == Role.ROLE_USER &&
                        u.isRegistered()
        ));
    }

    @Test
    void createUser_duplicateEmailThrowsConflict() {
        UserDTO.CreateUserDTO dto = UserDTO.CreateUserDTO.builder()
                .name("Bob")
                .email("bob@example.com")
                .password("pwd")
                .build();

        when(userRepo.findByEmail("bob@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(CustomConflictException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}