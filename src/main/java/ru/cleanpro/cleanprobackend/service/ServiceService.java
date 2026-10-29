package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;
import ru.cleanpro.cleanprobackend.model.ServiceEntity;
import ru.cleanpro.cleanprobackend.repository.CategoryRepository;
import ru.cleanpro.cleanprobackend.repository.ServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    // ===== Услуги =====

    @Transactional(readOnly = true)
    public List<ServiceEntity> getActiveServices() {
        return serviceRepository.findAllByActiveTrueOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAllWithCategoryOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<ServiceEntity> getActiveByCategory(CategoryEntity category) {
        return serviceRepository.findAllByCategoryAndActiveTrueOrderBySortOrderAsc(category);
    }

    @Transactional(readOnly = true)
    public ServiceEntity getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public ServiceEntity getBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена: " + slug));
    }

    @Transactional(readOnly = true)
    public List<ServiceEntity> searchServices(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return serviceRepository.searchByQuery(query.trim());
    }

    @Transactional
    public ServiceEntity save(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }

    // ===== Категории =====

    @Transactional(readOnly = true)
    public List<CategoryEntity> getActiveCategories() {
        List<CategoryEntity> categories = categoryRepository.findAllByActiveTrueOrderBySortOrderAsc();
        // Принудительно инициализируем ленивую коллекцию услуг, пока открыта транзакция
        categories.forEach(cat -> cat.getServices().size());
        return categories;
    }

    @Transactional(readOnly = true)
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public CategoryEntity getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + slug));
    }

    @Transactional
    public CategoryEntity saveCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}