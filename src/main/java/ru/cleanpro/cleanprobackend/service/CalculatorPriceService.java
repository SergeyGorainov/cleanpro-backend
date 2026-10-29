package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.model.CalculatorPriceEntity;
import ru.cleanpro.cleanprobackend.repository.CalculatorPriceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalculatorPriceService {

    private final CalculatorPriceRepository repository;

    @Transactional(readOnly = true)
    public List<CalculatorPriceEntity> getAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    // Возвращает Map<code, value> — то, что нужно отдать в JS
    @Transactional(readOnly = true)
    public Map<String, Double> getAsMap() {
        Map<String, Double> result = new HashMap<>();
        for (CalculatorPriceEntity p : repository.findAll()) {
            result.put(p.getCode(), p.getValue().doubleValue());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CalculatorPriceEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цена не найдена: " + id));
    }

    @Transactional
    public CalculatorPriceEntity save(CalculatorPriceEntity entity) {
        return repository.save(entity);
    }
}