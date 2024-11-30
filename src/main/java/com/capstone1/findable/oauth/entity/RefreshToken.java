package com.capstone1.findable.oauth.entity;

import com.capstone1.findable.User.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 65535, nullable = false)
    private String token; // 실제 리프레시 토큰 값

    @Column(nullable = false)
    private String deviceId; // 디바이스 또는 세션 식별자 추가

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 리프레시 토큰의 만료 날짜

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
