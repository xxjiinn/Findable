package com.capstone1.findable.Notice.controller;

import com.capstone1.findable.Notice.dto.NoticeDTO;
import com.capstone1.findable.Notice.service.NoticeService;
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

    @PostMapping("/createNotice")
    public ResponseEntity<Void> createNotice(@Valid @RequestBody NoticeDTO.CreateNoticeDTO dto){
        noticeService.createNotice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<List<NoticeDTO.ReadNoticeDTO>> findAllNotice(){
        List<NoticeDTO.ReadNoticeDTO> posts = noticeService.findAllNotice();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO.ReadNoticeDTO> findNoticeById(@PathVariable Long id){
        NoticeDTO.ReadNoticeDTO post = noticeService.findNoticeById(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateNoticeInfo(@PathVariable Long id, @RequestBody NoticeDTO.ReadNoticeDTO dto){
        noticeService.updateNoticeInfo(id, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoticeInfo(@PathVariable Long id){
        noticeService.deleteNoticeInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
