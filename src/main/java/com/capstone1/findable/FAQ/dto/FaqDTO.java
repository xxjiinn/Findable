package com.capstone1.findable.FAQ.dto;

import com.capstone1.findable.FAQ.entity.FAQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FaqDTO {

    @Getter
    public static class CreateFaqDTO{

        private String question;
        private String answer;
        private Long userId; // 참조관계
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadFaqDTO{
        private Long id;

        private String question;
        private String answer;
        private Long userId; // 참조 관계


        public static ReadFaqDTO toDTO(FAQ faq) {
            return ReadFaqDTO.builder()
                    .id(faq.getId())
                    .question(faq.getQuestion())
                    .answer(faq.getAnswer())
                    .userId(faq.getUser().getId()) // 참조 관계
                    .build();
        }
    }
}
