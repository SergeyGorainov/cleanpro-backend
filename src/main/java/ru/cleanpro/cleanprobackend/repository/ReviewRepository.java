package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.ReviewEntity;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByActiveTrueOrderByCreatedAtDesc();

    List<ReviewEntity> findAllByOrderByCreatedAtDesc();
}