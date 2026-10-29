package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.CalculatorPriceEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalculatorPriceRepository extends JpaRepository<CalculatorPriceEntity, Long> {

    List<CalculatorPriceEntity> findAllByOrderBySortOrderAsc();

    Optional<CalculatorPriceEntity> findByCode(String code);
}