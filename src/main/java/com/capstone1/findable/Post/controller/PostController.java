package com.capstone1.findable.Post.controller;

import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.Post.service.PostService;
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

    @PostMapping("/createPost")
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostDTO.CreatePostDTO dto){
        postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> findAllPost(){
        List<PostDTO.ReadPostDTO> posts = postService.findAllPost();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.ReadPostDTO> findPostById(@PathVariable Long id){
        PostDTO.ReadPostDTO post = postService.findPostById(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePostInfo(@PathVariable Long id, @RequestBody PostDTO.ReadPostDTO dto){
        postService.updatePostInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostInfo(@PathVariable Long id){
        postService.deletePostInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO.ReadPostDTO>> searchPosts(@RequestParam String query) {
        List<PostDTO.ReadPostDTO> searchResults = postService.searchPosts(query);
        return ResponseEntity.ok(searchResults);
    }

}
