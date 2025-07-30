package com.capstone1.findable.Post.service;

import com.capstone1.findable.Exception.PostAuthorizationException;
import com.capstone1.findable.Exception.PostNotFoundException;
import com.capstone1.findable.Exception.ResourceNotFoundException;
import com.capstone1.findable.Post.dto.PostDTO;
import com.capstone1.findable.Post.entity.Post;
import com.capstone1.findable.Post.repo.PostRepo;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepo postRepo;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    /** 게시글 생성 */
    public void createPost(PostDTO.CreatePostDTO dto) {
        logger.info("☑️ [CREATE POST] userId={}, title={}", dto.getUserId(), dto.getTitle());
        User user = userService.getUserEntityById(dto.getUserId());
        Post post = Post.fromDTO(dto, user);
        postRepo.save(post);
        logger.info("✅ [CREATE POST] 저장 완료 postId={}", post.getId());
    }

    /** 전체 게시글 조회 */
    @Transactional(readOnly = true)
    public List<PostDTO.ReadPostDTO> findAllPost() {
        logger.debug("☑️ [FIND ALL POSTS]");
        return postRepo.findAll().stream()
                .map(PostDTO.ReadPostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 사용자별 게시글 조회 */
    @Transactional(readOnly = true)
    public List<PostDTO.ReadPostDTO> findMyPosts(Long userId) {
        logger.debug("☑️ [FIND MY POSTS] userId={}", userId);
        return postRepo.findByUserId(userId).stream()
                .map(PostDTO.ReadPostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 단일 게시글 조회 */
    @Transactional(readOnly = true)
    public PostDTO.ReadPostDTO findPostById(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + id));
        return PostDTO.ReadPostDTO.fromEntity(post);
    }

    /** 게시글 수정 */
    public void updatePostInfo(Long id, PostDTO.ReadPostDTO dto) {
        logger.info("☑️ [UPDATE POST] id={}", id);
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            post.setContent(dto.getContent());
        }
        if (dto.getUrl() != null && !dto.getUrl().isBlank()) {
            post.setUrl(dto.getUrl());
        }
        postRepo.save(post);
        logger.info("✅ [UPDATE POST] 수정 완료 id={}", id);
    }

    /** 게시글 삭제 */
    public void deletePostInfo(Long id) {
        logger.info("☑️ [DELETE POST] id={}", id);
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + id));
        postRepo.delete(post);
        logger.info("✅ [DELETE POST] 삭제 완료 id={}", id);
    }

    /** 게시글 검색 */
    @Transactional(readOnly = true)
    public List<PostDTO.ReadPostDTO> searchPosts(String query) {
        logger.debug("☑️ [SEARCH POSTS] query={}", query);
        return postRepo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query).stream()
                .map(PostDTO.ReadPostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 게시글 소유권 검증 */
    @Transactional(readOnly = true)
    public void verifyPostOwnership(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));
        if (!post.getUser().getId().equals(userId)) {
            logger.warn("❌ [AUTH] userId={} cannot modify postId={}", userId, postId);
            throw new PostAuthorizationException("해당 게시글에 대한 권한이 없습니다.");
        }
    }
}