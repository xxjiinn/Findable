package com.capstone1.findable.FAQ.entity;

import com.capstone1.findable.FAQ.dto.FaqDTO;
import com.capstone1.findable.User.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String answer;

    @Enumerated(EnumType.STRING) // 카테고리 Enum 타입으로 처리
    private FaqCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static FAQ toEntity(FaqDTO.CreateFaqDTO dto, User user) {
        return FAQ.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .category(dto.getCategory()) // 카테고리 설정 추가
                .user(user) // 참조관계
                .build();
    }
}
