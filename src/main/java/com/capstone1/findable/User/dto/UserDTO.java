package com.capstone1.findable.User.dto;


import com.capstone1.findable.User.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Getter
    public static class CreateUserDTO {
        @NotEmpty
        private String name;

        @NotEmpty
        private String email;

        @NotEmpty
        private String password;
    }

    @Getter
    public static class LoginDTO {
        private String email;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadUserDTO {
        private Long id;

        private String name;
        private String email;
        private String password;

        public static ReadUserDTO toDTO(User user) {
            return ReadUserDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();
        }
    }
}
