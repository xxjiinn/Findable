package com.capstone1.findable.Post.controller;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.Post.service.PostService;
import com.capstone1.findable.config.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    /** 게시글 생성 */
    @PostMapping("/{userId}")
    public ResponseEntity<Void> createPost(
            @PathVariable Long userId,
            @Valid @RequestBody PostDTO.CreatePostDTO dto
    ) {
        logger.info("🍀 [CREATE POST] title={}, userId={}", dto.getTitle(), userId);
        dto.setUserId(userId);
        postService.createPost(dto);
        logger.info("✅ [CREATE POST] 완료 userId={}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 전체 게시글 조회 */
    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> findAllPost() {
        logger.info("🍀 [FIND ALL POSTS]");
        List<PostDTO.ReadPostDTO> posts = postService.findAllPost();
        logger.info("✅ [FIND ALL POSTS] count={}", posts.size());
        return ResponseEntity.ok(posts);
    }

    /** 내 게시글 조회 */
    @GetMapping("/myPosts")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> findMyPosts(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.getId();
        logger.info("🍀 [FIND MY POSTS] userId={}", userId);
        List<PostDTO.ReadPostDTO> posts = postService.findMyPosts(userId);
        logger.info("✅ [FIND MY POSTS] userId={}, count={}", userId, posts.size());
        return ResponseEntity.ok(posts);
    }

    /** ID로 게시글 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.ReadPostDTO> findPostById(@PathVariable Long id) {
        logger.info("🍀 [FIND POST] id={}", id);
        PostDTO.ReadPostDTO post = postService.findPostById(id);
        return ResponseEntity.ok(post);
    }

    /** 게시글 수정 */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDTO.ReadPostDTO dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.getId();
        logger.info("🍀 [UPDATE POST] id={}, userId={}", id, userId);
        postService.verifyPostOwnership(id, userId);
        postService.updatePostInfo(id, dto);
        logger.info("✅ [UPDATE POST] 완료 id={}, userId={}", id, userId);
        return ResponseEntity.noContent().build();
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = principal.getId();
        logger.info("🍀 [DELETE POST] id={}, userId={}", id, userId);
        postService.verifyPostOwnership(id, userId);
        postService.deletePostInfo(id);
        logger.info("✅ [DELETE POST] 완료 id={}, userId={}", id, userId);
        return ResponseEntity.noContent().build();
    }

    /** 게시글 검색 */
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> searchPosts(@RequestParam String query) {
        logger.info("🍀 [SEARCH POSTS] query={}", query);
        List<PostDTO.ReadPostDTO> results = postService.searchPosts(query);
        return ResponseEntity.ok(results);
    }
}
