package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.FaqEntity;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Long> {

    List<FaqEntity> findAllByActiveTrueOrderBySortOrderAsc();

    List<FaqEntity> findAllByOrderBySortOrderAsc();
}