package com.capstone1.findable.Post.repo;

import com.capstone1.findable.Post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Long> {
}
