package com.capstone1.findable.Post.service;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.Post.entity.Post;
import com.capstone1.findable.Post.repo.PostRepo;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public void createPost(PostDTO.CreatePostDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ User not found!"));

        Post post = Post.toEntity(dto, user);
        postRepo.save(post);
    }

    public List<PostDTO.ReadPostDTO> findAllPost() {
        return postRepo.findAll()
                .stream()
                .map(PostDTO.ReadPostDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<PostDTO.ReadPostDTO> findMyPosts(Long userId) {
        return postRepo.findByUserId(userId)
                .stream()
                .map(PostDTO.ReadPostDTO::toDTO)
                .collect(Collectors.toList());
    }

    public PostDTO.ReadPostDTO findPostById(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Post found! (while reading post)"));
        return PostDTO.ReadPostDTO.toDTO(post);
    }

    public void updatePostInfo(Long id, PostDTO.ReadPostDTO dto) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Post found! (while updating post)"));
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            post.setContent(dto.getContent());
        }
        if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
            post.setUrl(dto.getUrl());
        }

        postRepo.save(post);
    }

    public void deletePostInfo(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Post found! (while deleting post)"));
        postRepo.delete(post);
    }

    public List<PostDTO.ReadPostDTO> searchPosts(String query) {
        return postRepo.findByTitleContainingOrContentContaining(query, query)
                .stream()
                .map(PostDTO.ReadPostDTO::toDTO)
                .collect(Collectors.toList());
    }

    public void verifyPostOwnership(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Post found!"));
        if (!Long.valueOf(post.getUser().getId()).equals(userId)) { // 수정: Long.valueOf()로 비교
            throw new SecurityException("⚠️ You do not have permission to modify or delete this post.");
        }
    }
}
