package com.capstone1.findable.Post.controller;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.Post.service.PostService;
import com.capstone1.findable.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider; // Access Token에서 정보 추출

    @PostMapping("/createPost")
    public ResponseEntity<Void> createPost(
            @Valid @RequestBody PostDTO.CreatePostDTO dto,
            HttpServletRequest request
    ) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        dto.setUserId(userId); // 추출한 userId를 DTO에 설정
        postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> findAllPost() {
        List<PostDTO.ReadPostDTO> posts = postService.findAllPost();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/myPosts")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> findMyPosts(HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        List<PostDTO.ReadPostDTO> myPosts = postService.findMyPosts(userId);
        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.ReadPostDTO> findPostById(@PathVariable Long id) {
        PostDTO.ReadPostDTO post = postService.findPostById(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePostInfo(
            @PathVariable Long id,
            @RequestBody PostDTO.ReadPostDTO dto,
            HttpServletRequest request
    ) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        postService.verifyPostOwnership(id, userId); // 게시물 소유 여부 확인
        postService.updatePostInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostInfo(@PathVariable Long id, HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        postService.verifyPostOwnership(id, userId); // 게시물 소유 여부 확인
        postService.deletePostInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> searchPosts(@RequestParam String query) {
        List<PostDTO.ReadPostDTO> searchResults = postService.searchPosts(query);
        return ResponseEntity.ok(searchResults);
    }

    // Access Token 추출을 위한 유틸리티 메서드 추가
    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // "Bearer " 제거
        }
        throw new IllegalArgumentException("⚠️ Authorization token is missing or invalid.");
    }
}