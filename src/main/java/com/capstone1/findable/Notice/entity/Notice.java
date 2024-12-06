package com.capstone1.findable.Notice.entity;

import com.capstone1.findable.Notice.dto.NoticeDTO;
import com.capstone1.findable.User.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING) // 카테고리 Enum 타입
    private Category category;

    @Column(nullable = false)
    private int viewCount; // 조회수 초기값은 0

    // 공지사항 카테고리 Enum 정의
    public enum Category {
        GENERAL, // 일반 공지
        SYSTEM, // 시스템 점검
        EVENT   // 이벤트 공지
    }

    public static Notice toEntity(NoticeDTO.CreateNoticeDTO dto, User user) {
        return Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user) // 참조 관계
                .category(dto.getCategory()) // 카테고리 추가
                .viewCount(0) // 조회수 초기화
                .build();
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }
}
