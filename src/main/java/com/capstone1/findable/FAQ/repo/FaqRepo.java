package com.capstone1.findable.FAQ.repo;

import com.capstone1.findable.FAQ.entity.FAQ;
import com.capstone1.findable.FAQ.entity.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepo extends JpaRepository<FAQ, Long> {
    List<FAQ> findByCategory(FaqCategory category);
    List<FAQ> findByQuestionContainingOrAnswerContaining(String question, String answer);
}
