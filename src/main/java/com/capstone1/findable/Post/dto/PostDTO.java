package com.capstone1.findable.Post.dto;

import com.capstone1.findable.Post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostDTO {

    @Getter
    public static class CreatePostDTO {
        private String title;
        private String content;
        private Long userId;
        private String url; // URL 필드 추가
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
        private String url; // URL 필드 추가

        public static ReadPostDTO toDTO(Post post) {
            return ReadPostDTO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .userId(post.getUser().getId())
                    .url(post.getUrl()) // URL 필드 매핑
                    .build();
        }
    }
}
