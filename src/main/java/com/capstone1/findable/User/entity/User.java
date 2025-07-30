package com.capstone1.findable.User.entity;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.FAQ.entity.FAQ;
import com.capstone1.findable.Notice.entity.Notice;
import com.capstone1.findable.Post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "username")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"posts", "notices", "faqs"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean registered;  // 회원가입 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // Enum으로 역할 관리

    @Column(length = 50)
    private String provider;  // OAuth2 provider

    @Column(length = 100)
    private String providerId;  // OAuth2 provider ID

    private LocalDateTime loginDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;  // 생성 시각 자동 기록

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // 수정 시각 자동 기록

    @JsonIgnore
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();  // 게시물 목록

    @JsonIgnore
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();  // 공지사항 목록

    @JsonIgnore
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FAQ> faqs = new ArrayList<>();  // FAQ 목록

    /** 새로운 User 엔티티 생성 */
    public static User fromDTO(UserDTO.CreateUserDTO dto, String encodedPassword) {
        return User.builder()
                .name(dto.getName())
                .username(dto.getName())
                .password(encodedPassword)
                .email(dto.getEmail())
                .role(Role.ROLE_USER)
                .registered(true)
                .build();
    }

    /** 프로필 정보 수정 */
    public void updateProfile(String name, String email) {
        if (name != null && !name.isBlank()) this.name = name;
        if (email != null && !email.isBlank()) this.email = email;
    }

    /** 비밀번호 변경 */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
