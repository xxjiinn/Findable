package com.capstone1.findable.Post.repo;

import com.capstone1.findable.Post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrContentContaining(String title, String content);

    List<Post> findByUserId(Long userId);

}