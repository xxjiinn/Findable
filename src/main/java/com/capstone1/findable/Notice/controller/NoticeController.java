package com.capstone1.findable.Notice.controller;

import com.capstone1.findable.Notice.dto.NoticeDTO;
import com.capstone1.findable.Notice.entity.Notice.Category;
import com.capstone1.findable.Notice.service.NoticeService;
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
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 검증 및 정보 추출

    @PostMapping("/createNotice")
    public ResponseEntity<Void> createNotice(
            @Valid @RequestBody NoticeDTO.CreateNoticeDTO dto,
            HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        dto.setUserId(userId); // DTO에 userId 설정
        noticeService.createNotice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<List<NoticeDTO.ReadNoticeDTO>> findAllNotice(
            @RequestParam(value = "category", required = false) Category category) {
        List<NoticeDTO.ReadNoticeDTO> notices = noticeService.findAllNotice(category);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO.ReadNoticeDTO> findNoticeById(@PathVariable Long id, HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        NoticeDTO.ReadNoticeDTO notice = noticeService.findNoticeById(id, userId); // 조회수 트래킹 포함
        return ResponseEntity.ok(notice);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateNoticeInfo(
            @PathVariable Long id,
            @RequestBody NoticeDTO.ReadNoticeDTO dto,
            HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        noticeService.verifyNoticeOwnership(id, userId); // 소유권 확인
        noticeService.updateNoticeInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoticeInfo(@PathVariable Long id, HttpServletRequest request) {
        String token = extractTokenFromRequest(request); // Access Token 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token); // userId 추출
        noticeService.verifyNoticeOwnership(id, userId); // 소유권 확인
        noticeService.deleteNoticeInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Access Token 추출을 위한 유틸리티 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // "Bearer " 제거
        }
        throw new IllegalArgumentException("⚠️ Authorization token is missing or invalid.");
    }
}
