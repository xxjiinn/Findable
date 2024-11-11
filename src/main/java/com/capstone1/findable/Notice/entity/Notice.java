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

    public static Notice toEntity(NoticeDTO.CreateNoticeDTO dto, User user) {
        return Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user) // 참조관계
                .build();
    }
}
