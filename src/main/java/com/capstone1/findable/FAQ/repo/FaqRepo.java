package com.capstone1.findable.FAQ.repo;

import com.capstone1.findable.FAQ.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepo extends JpaRepository<FAQ, Long> {
}
