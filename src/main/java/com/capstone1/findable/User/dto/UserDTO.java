package com.capstone1.findable.User.dto;

import com.capstone1.findable.User.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

/** User 관련 DTO 모음 */
public class UserDTO {

    /** 회원 가입 요청 DTO */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateUserDTO {
        @NotBlank(message = "Name is mandatory")
        @Size(max = 50, message = "Name must be at most 50 characters")
        private String name;  // 사용자 이름

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        private String password;  // 비밀번호

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must be at most 100 characters")
        private String email;  // 이메일 주소
    }

    /** 로그인 요청 DTO */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginUserDTO {
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        private String email;  // 이메일

        @NotBlank(message = "Password is mandatory")
        private String password;  // 비밀번호
    }

    /** 사용자 프로필 응답 DTO */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDTO {
        private Long id;  // 사용자 ID
        private String name;  // 사용자 이름
        private String email;  // 이메일
        private LocalDateTime joinedAt;  // 가입 시각
        private String role;  // 사용자 역할

        /** 엔티티를 ProfileDTO로 변환 */
        public static ProfileDTO fromEntity(User user) {
            return ProfileDTO.builder()
                    .id(user.getId())
                    .name(user.getUsername())
                    .email(user.getEmail())
                    .joinedAt(user.getCreatedAt())
                    .role(user.getRole().name())
                    .build();
        }
    }

    /** 사용자 정보 조회 및 수정 DTO */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadUserDTO {
        private Long id;  // 사용자 ID

        @NotBlank(message = "Name is mandatory")
        @Size(max = 50, message = "Name must be at most 50 characters")
        private String name;  // 사용자 이름

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        private String password;  // 비밀번호

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must be at most 100 characters")
        private String email;  // 이메일 주소

        private LocalDateTime createdAt;  // 생성 시각
        private LocalDateTime updatedAt;  // 수정 시각

        /** 엔티티를 ReadUserDTO로 변환 */
        public static ReadUserDTO fromEntity(User user) {
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
