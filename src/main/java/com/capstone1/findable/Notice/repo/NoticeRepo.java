package com.capstone1.findable.Notice.repo;

import com.capstone1.findable.Notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepo extends JpaRepository<Notice, Long> {
}
