package com.capstone1.findable.FAQ.dto;

import com.capstone1.findable.FAQ.entity.FAQ;
import com.capstone1.findable.FAQ.entity.FaqCategory;
import lombok.*;

public class FaqDTO {

    @Getter
    @Setter
    public static class CreateFaqDTO {
        private String question;
        private String answer;
        private FaqCategory category; // 카테고리 필드 추가
        private Long userId; // 참조관계
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadFaqDTO {
        private Long id;
        private String question;
        private String answer;
        private FaqCategory category; // 카테고리 필드 추가
        private Long userId; // 참조 관계

        public static ReadFaqDTO toDTO(FAQ faq) {
            return ReadFaqDTO.builder()
                    .id(faq.getId())
                    .question(faq.getQuestion())
                    .answer(faq.getAnswer())
                    .category(faq.getCategory()) // 카테고리 변환 추가
                    .userId(faq.getUser().getId()) // 참조 관계
                    .build();
        }
    }
}
