package com.capstone1.findable.Post.dto;

import com.capstone1.findable.Post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class PostDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePostDTO {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 최대 100자까지 허용됩니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        @Size(max = 200, message = "URL은 최대 200자까지 허용됩니다.")
        private String url;

        @Setter
        private Long userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReadPostDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String url;

        /** 엔티티를 DTO로 변환 */
        public static ReadPostDTO fromEntity(Post post) {
            return ReadPostDTO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .userId(post.getUser().getId())
                    .url(post.getUrl())
                    .build();
        }
    }
}