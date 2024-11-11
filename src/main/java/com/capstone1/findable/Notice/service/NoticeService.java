package com.capstone1.findable.Notice.service;

import com.capstone1.findable.Notice.dto.NoticeDTO;
import com.capstone1.findable.Notice.entity.Notice;
import com.capstone1.findable.Notice.repo.NoticeRepo;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepo noticeRepo;
    private final UserRepo userRepo;

    public void createNotice(NoticeDTO.CreateNoticeDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ User not found! (while creating notice)")); // 참조관계
        noticeRepo.save(Notice.toEntity(dto, user)); // 참조 관계
    }

    public List<NoticeDTO.ReadNoticeDTO> findAllNotice(){
        return noticeRepo.findAll()
                .stream()
                .map(NoticeDTO.ReadNoticeDTO::toDTO)
                .collect(Collectors.toList());
    }

    public NoticeDTO.ReadNoticeDTO findNoticeById(Long id){
        Notice notice = noticeRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while reading nost"));
        return NoticeDTO.ReadNoticeDTO.toDTO(notice);
    }

    public void updateNoticeInfo(Long id, NoticeDTO.ReadNoticeDTO dto){
        Notice notice = noticeRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while updating notice"));
        if(dto.getTitle() != null && !dto.getTitle().isEmpty()){
            notice.setTitle(dto.getTitle());
        }
        if(dto.getContent() != null && !dto.getContent().isEmpty()){
            notice.setContent(dto.getContent());
        }

        noticeRepo.save(notice);
    }

    public void deleteNoticeInfo(Long id){
        Notice notice = noticeRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while deleting notice"));
        noticeRepo.delete(notice);
    }

}
