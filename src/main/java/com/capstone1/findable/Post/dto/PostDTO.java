package com.capstone1.findable.Post.dto;

import com.capstone1.findable.Post.entity.Post;
import lombok.*;

public class PostDTO {

    @Getter
    @Setter
    public static class CreatePostDTO {
        private String title;
        private String content;
        private String url;
        // PostController 내부에서만 설정 가능
        @Setter
        private Long userId; // userId는 외부에서 설정하지 않음

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

        public static ReadPostDTO toDTO(Post post) {
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