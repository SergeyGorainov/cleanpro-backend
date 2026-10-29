package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findAllByActiveTrueOrderBySortOrderAsc();

    List<CategoryEntity> findAllByOrderBySortOrderAsc();

    Optional<CategoryEntity> findBySlug(String slug);
}