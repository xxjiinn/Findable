package com.capstone1.findable.User.entity;

import com.capstone1.findable.FAQ.entity.FAQ;
import com.capstone1.findable.Notice.entity.Notice;
import com.capstone1.findable.Post.entity.Post;
import com.capstone1.findable.User.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String username;
    private String password;
    private String email;
    private boolean registered;  // 회원가입 여부 /

    @Enumerated(EnumType.STRING)
    private Role role;  // Enum으로 역할 관리

    private String provider;    // OAuth2 provider (예: Google)
    private String providerId;  // OAuth2 provider ID
    private LocalDateTime loginDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FAQ> faqs = new ArrayList<>();

    public static User toEntity(UserDTO.CreateUserDTO dto) {
        return User.builder()
                .username(dto.getName())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .role(Role.ROLE_USER) // 기본 권한 설정 (사용자 등록 시 기본 USER 권한)
                .build();
    }
}
