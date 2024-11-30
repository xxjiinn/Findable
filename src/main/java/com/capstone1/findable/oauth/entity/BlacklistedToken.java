package com.capstone1.findable.oauth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // 로그아웃된 Refresh Token

    @Column(nullable = false)
    private LocalDateTime blacklistedAt; // 블랙리스트에 추가된 시간
}