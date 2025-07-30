package com.capstone1.findable.Post.entity;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.User.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 200)
    private String url;

    /** DTO로부터 엔티티 생성 */
    public static Post fromDTO(PostDTO.CreatePostDTO dto, User user) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .url(dto.getUrl())
                .user(user)
                .build();
    }
}