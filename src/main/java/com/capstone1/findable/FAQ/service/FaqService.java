package com.capstone1.findable.FAQ.service;

import com.capstone1.findable.FAQ.dto.FaqDTO;
import com.capstone1.findable.FAQ.entity.FAQ;
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

    public void createFaq(FaqDTO.CreateFaqDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ User not found! (while creating faq)")); // 참조관계
        faqRepo.save(FAQ.toEntity(dto, user)); // 참조 관계
    }

    public List<FaqDTO.ReadFaqDTO> findAllFaq(){
        return faqRepo.findAll()
                .stream()
                .map(FaqDTO.ReadFaqDTO::toDTO)
                .collect(Collectors.toList());
    }

    public FaqDTO.ReadFaqDTO findFaqById(Long id){
        FAQ faq = faqRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No faq found! (while reading faq"));
        return FaqDTO.ReadFaqDTO.toDTO(faq);
    }

    public void updateFaqInfo(Long id, FaqDTO.ReadFaqDTO dto){
        FAQ faq = faqRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No faq found! (while updating faq"));
        if(dto.getQuestion() != null && !dto.getQuestion().isEmpty()){
            faq.setQuestion(dto.getQuestion());
        }
        if(dto.getAnswer() != null && !dto.getAnswer().isEmpty()){
            faq.setAnswer(dto.getAnswer());
        }

        faqRepo.save(faq);
    }

    public void deleteFaqInfo(Long id){
        FAQ faq = faqRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No faq found! (while deleting faq"));
        faqRepo.delete(faq);
    }

}
