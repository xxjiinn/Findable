package com.capstone1.findable.Post.entity;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.User.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String url;

    public static Post toEntity(PostDTO.CreatePostDTO dto, User user) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .url(dto.getUrl())
                .user(user)
                .build();
    }
}
