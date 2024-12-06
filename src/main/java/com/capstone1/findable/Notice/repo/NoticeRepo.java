package com.capstone1.findable.Notice.repo;

import com.capstone1.findable.Notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoticeRepo extends JpaRepository<Notice, Long> {
    // 카테고리별 공지사항 검색
    List<Notice> findByCategory(Notice.Category category);

    // 특정 키워드를 제목 또는 내용에서 검색
    List<Notice> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

    // 카테고리와 키워드 조합 검색
    List<Notice> findByCategoryAndTitleContainingOrContentContaining(Notice.Category category, String titleKeyword, String contentKeyword);
}
