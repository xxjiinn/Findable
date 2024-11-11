package com.capstone1.findable.Notice.dto;

import com.capstone1.findable.Notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeDTO {

    @Getter
    public static class CreateNoticeDTO{

        private String title;
        private String content;
        private Long userId; // 참조관계
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadNoticeDTO{
        private Long id;

        private String title;
        private String content;
        private Long userId; // 참조 관계


        public static ReadNoticeDTO toDTO(Notice notice) {
            return ReadNoticeDTO.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .userId(notice.getUser().getId()) // 참조 관계
                    .build();
        }
    }
}
