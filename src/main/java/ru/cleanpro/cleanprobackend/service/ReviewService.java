package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.model.ReviewEntity;
import ru.cleanpro.cleanprobackend.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<ReviewEntity> getActiveReviews() {
        return reviewRepository.findAllByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public ReviewEntity getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден: " + id));
    }

    @Transactional
    public ReviewEntity save(ReviewEntity review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}