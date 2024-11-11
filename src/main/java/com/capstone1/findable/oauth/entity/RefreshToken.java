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

    @ManyToOne(fetch = FetchType.LAZY) // 한 유저가 여러 Refresh Token을 가질 수 있음
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 65535) // 토큰 길이를 충분히 넓게 설정
    private String token; // 실제 리프레시 토큰 값
    private LocalDateTime expiryDate; // 리프레시 토큰의 만료 날짜

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
