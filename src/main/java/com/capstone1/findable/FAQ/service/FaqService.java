package com.capstone1.findable.FAQ.service;

import com.capstone1.findable.FAQ.dto.FaqDTO;
import com.capstone1.findable.FAQ.entity.FAQ;
import com.capstone1.findable.FAQ.entity.FaqCategory;
import com.capstone1.findable.FAQ.repo.FaqRepo;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepo faqRepo;
    private final UserRepo userRepo;

    public void createFaq(FaqDTO.CreateFaqDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ User not found!"));

        FAQ faq = FAQ.toEntity(dto, user);
        faqRepo.save(faq);
    }

    public List<FaqDTO.ReadFaqDTO> findAllFaq() {
        return faqRepo.findAll()
                .stream()
                .map(FaqDTO.ReadFaqDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<FaqDTO.ReadFaqDTO> findFaqsByCategory(String category) {
        try {
            FaqCategory faqCategory = FaqCategory.valueOf(category.toUpperCase());
            return faqRepo.findByCategory(faqCategory)
                    .stream()
                    .map(FaqDTO.ReadFaqDTO::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("⚠️ Invalid category value: " + category);
        }
    }

    public List<FaqDTO.ReadFaqDTO> searchFaqs(String query) {
        return faqRepo.findByQuestionContainingOrAnswerContaining(query, query)
                .stream()
                .map(FaqDTO.ReadFaqDTO::toDTO)
                .collect(Collectors.toList());
    }

    public FaqDTO.ReadFaqDTO findFaqById(Long id) {
        FAQ faq = faqRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No FAQ found! (while reading FAQ)"));
        return FaqDTO.ReadFaqDTO.toDTO(faq);
    }

    public void updateFaqInfo(Long id, FaqDTO.ReadFaqDTO dto) {
        FAQ faq = faqRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No FAQ found! (while updating FAQ)"));

        if (dto.getQuestion() != null && !dto.getQuestion().isEmpty()) {
            faq.setQuestion(dto.getQuestion());
        }
        if (dto.getAnswer() != null && !dto.getAnswer().isEmpty()) {
            faq.setAnswer(dto.getAnswer());
        }

        faqRepo.save(faq);
    }

    public void deleteFaqInfo(Long id) {
        FAQ faq = faqRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No FAQ found! (while deleting FAQ)"));
        faqRepo.delete(faq);
    }

    public void verifyFaqOwnership(Long faqId, Long userId) {
        FAQ faq = faqRepo.findById(faqId)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No FAQ found!"));
        if (!Long.valueOf(faq.getUser().getId()).equals(userId)) {
            throw new SecurityException("⚠️ You do not have permission to modify or delete this FAQ.");
        }
    }
}
