package com.capstone1.findable.Notice.service;

import com.capstone1.findable.Notice.dto.NoticeDTO;
import com.capstone1.findable.Notice.entity.Notice;
import com.capstone1.findable.Notice.repo.NoticeRepo;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.Exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepo noticeRepo;
    private final UserRepo userRepo;

    // 공지사항 생성
    public void createNotice(NoticeDTO.CreateNoticeDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ User not found! (while creating notice)"));
        Notice notice = Notice.toEntity(dto, user);
        noticeRepo.save(notice);
    }

    // 공지사항 목록 조회
    public List<NoticeDTO.ReadNoticeDTO> findAllNotice(Notice.Category category) {
        return (category == null ? noticeRepo.findAll() : noticeRepo.findByCategory(category))
                .stream()
                .map(NoticeDTO.ReadNoticeDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 공지사항 단건 조회 및 조회수 증가
    public NoticeDTO.ReadNoticeDTO findNoticeById(Long id, Long userId) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while reading notice)"));

        // 작성자가 아닌 경우 조회수 증가
        if (!Long.valueOf(notice.getUser().getId()).equals(userId)) {
            notice.incrementViewCount();
            noticeRepo.save(notice);
        }

        return NoticeDTO.ReadNoticeDTO.toDTO(notice);
    }

    // 공지사항 수정
    public void updateNoticeInfo(Long id, NoticeDTO.ReadNoticeDTO dto) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while updating notice)"));

        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            notice.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            notice.setContent(dto.getContent());
        }
        if (dto.getCategory() != null) {
            notice.setCategory(dto.getCategory());
        }

        noticeRepo.save(notice);
    }

    // 공지사항 삭제
    public void deleteNoticeInfo(Long id) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found! (while deleting notice)"));
        noticeRepo.delete(notice);
    }

    // 공지사항 소유권 확인
    public void verifyNoticeOwnership(Long noticeId, Long userId) {
        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No Notice found!"));
        if (!Long.valueOf(notice.getUser().getId()).equals(userId)) {
            throw new UnauthorizedAccessException("⚠️ You do not have permission to modify or delete this notice.");
        }
    }

    // 특정 키워드 검색
    public List<NoticeDTO.ReadNoticeDTO> searchNotices(String query, Notice.Category category) {
        List<Notice> results = (category == null)
                ? noticeRepo.findByTitleContainingOrContentContaining(query, query)
                : noticeRepo.findByCategoryAndTitleContainingOrContentContaining(category, query, query);

        return results.stream()
                .map(NoticeDTO.ReadNoticeDTO::toDTO)
                .collect(Collectors.toList());
    }
}
