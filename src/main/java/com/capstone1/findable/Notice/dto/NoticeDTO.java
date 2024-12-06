package com.capstone1.findable.Notice.dto;

import com.capstone1.findable.Notice.entity.Notice;
import lombok.*;

public class NoticeDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateNoticeDTO {
        private String title;
        private String content;
        private Long userId; // 참조 관계
        private Notice.Category category; // 카테고리 추가
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadNoticeDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId; // 참조 관계
        private Notice.Category category; // 카테고리 추가
        private int views; // 조회수 추가

        public static ReadNoticeDTO toDTO(Notice notice) {
            return ReadNoticeDTO.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .userId(notice.getUser().getId()) // 참조 관계
                    .category(notice.getCategory()) // 카테고리 매핑
                    .views(notice.getViewCount()) // 조회수 매핑
                    .build();
        }
    }
}
