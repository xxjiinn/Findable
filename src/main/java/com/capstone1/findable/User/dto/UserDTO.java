package com.capstone1.findable.User.dto;

import com.capstone1.findable.User.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class UserDTO {

    @Getter
    @Setter
    @Builder
    public static class CreateUserDTO {
        private String name;
        private String password;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // 로그인 DTO
    @Getter
    @Setter
    public static class LoginUserDTO {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class ProfileDTO {
        private String name;
        private String email;
        private LocalDateTime joinedAt;
        private String role;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadUserDTO {
        private Long id;
        @NotBlank(message = "Name is mandatory")
        @Size(max = 20, message = "20자 제한")
        private String name;

        @NotBlank(message = "Password is mandatory")
        @Size(max = 20, message = "20자 제한")
        private String password;

        @NotBlank(message = "Email is mandatory")
        @Size(max = 30, message = "30자 제한")
        private String email;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReadUserDTO toDTO(User user) {
            return ReadUserDTO.builder()
                    .id(user.getId())
                    .name(user.getUsername())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}
