package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.model.FaqEntity;
import ru.cleanpro.cleanprobackend.repository.FaqRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FaqEntity> getActiveFaqs() {
        return faqRepository.findAllByActiveTrueOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<FaqEntity> getAllFaqs() {
        return faqRepository.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public FaqEntity getById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Вопрос не найден: " + id));
    }

    @Transactional
    public FaqEntity save(FaqEntity faq) {
        return faqRepository.save(faq);
    }

    @Transactional
    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}