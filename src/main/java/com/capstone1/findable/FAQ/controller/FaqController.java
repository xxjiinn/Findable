package com.capstone1.findable.FAQ.controller;

import com.capstone1.findable.FAQ.dto.FaqDTO;
import com.capstone1.findable.FAQ.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class FaqController {

    private final FaqService faqService;

    @PostMapping("/createPost")
    public ResponseEntity<Void> createPost(@Valid @RequestBody FaqDTO.CreateFaqDTO dto){
        faqService.createFaq(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<List<FaqDTO.ReadFaqDTO>> findAllFaq(){
        List<FaqDTO.ReadFaqDTO> posts = faqService.findAllFaq();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqDTO.ReadFaqDTO> findFaqById(@PathVariable Long id){
        FaqDTO.ReadFaqDTO post = faqService.findFaqById(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFaqInfo(@PathVariable Long id, @RequestBody FaqDTO.ReadFaqDTO dto){
        faqService.updateFaqInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaqInfo(@PathVariable Long id){
        faqService.deleteFaqInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
