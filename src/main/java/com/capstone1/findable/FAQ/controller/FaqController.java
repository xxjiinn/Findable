package com.capstone1.findable.FAQ.controller;

import com.capstone1.findable.FAQ.dto.FaqDTO;
import com.capstone1.findable.FAQ.service.FaqService;
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
@RequestMapping("/api/faq")
public class FaqController {

    private final FaqService faqService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/createFaq")
    public ResponseEntity<Void> createFaq(
            @Valid @RequestBody FaqDTO.CreateFaqDTO dto,
            HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        dto.setUserId(userId); // 유저 정보 설정
        faqService.createFaq(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<List<FaqDTO.ReadFaqDTO>> findAllFaq() {
        List<FaqDTO.ReadFaqDTO> faqs = faqService.findAllFaq();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/category")
    public ResponseEntity<List<FaqDTO.ReadFaqDTO>> findFaqsByCategory(@RequestParam String category) {
        List<FaqDTO.ReadFaqDTO> faqs = faqService.findFaqsByCategory(category);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FaqDTO.ReadFaqDTO>> searchFaqs(@RequestParam String query) {
        List<FaqDTO.ReadFaqDTO> searchResults = faqService.searchFaqs(query);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqDTO.ReadFaqDTO> findFaqById(@PathVariable Long id) {
        FaqDTO.ReadFaqDTO faq = faqService.findFaqById(id);
        return ResponseEntity.ok(faq);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFaqInfo(
            @PathVariable Long id,
            @RequestBody FaqDTO.ReadFaqDTO dto,
            HttpServletRequest request
    ) {
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        faqService.verifyFaqOwnership(id, userId); // FAQ 소유권 확인
        faqService.updateFaqInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaqInfo(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            faqService.verifyFaqOwnership(id, userId);
            faqService.deleteFaqInfo(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 권한 없음
        }
    }


    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new IllegalArgumentException("⚠️ Authorization token is missing or invalid.");
    }
}
