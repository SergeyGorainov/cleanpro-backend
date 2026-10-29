package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;
import ru.cleanpro.cleanprobackend.model.ServiceEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findAllByActiveTrueOrderBySortOrderAsc();

    // С подгрузкой категории — чтобы не было LazyInitializationException
    @Query("SELECT s FROM ServiceEntity s LEFT JOIN FETCH s.category ORDER BY s.sortOrder ASC")
    List<ServiceEntity> findAllWithCategoryOrderBySortOrderAsc();

    List<ServiceEntity> findAllByCategoryAndActiveTrueOrderBySortOrderAsc(CategoryEntity category);

    List<ServiceEntity> findAllByCategoryOrderBySortOrderAsc(CategoryEntity category);

    Optional<ServiceEntity> findBySlug(String slug);


    // Полнотекстовый поиск по услугам.
    // Ищет по названию, короткому и полному описанию. Без учёта регистра.

    @Query("SELECT s FROM ServiceEntity s " +
            "WHERE s.active = true AND (" +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.shortDescription) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.fullDescription) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY s.sortOrder ASC")
    List<ServiceEntity> searchByQuery(@Param("query") String query);
}