package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.dto.OrderCreateDto;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.model.ServiceEntity;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.repository.OrderRepository;
import ru.cleanpro.cleanprobackend.repository.ServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final EmailService emailService;


    // Создание заявки от гостя (без привязки к пользователю).

    @Transactional
    public OrderEntity createOrder(OrderCreateDto dto) {
        return createOrder(dto, null);
    }


    // Создание заявки с привязкой к пользователю (если он авторизован).
    // После сохранения отправляет письма: администратору и клиенту.

    @Transactional
    public OrderEntity createOrder(OrderCreateDto dto, UserEntity user) {
        // Ищем услугу по slug, который пришёл из формы
        ServiceEntity service = null;
        if (dto.getService() != null && !dto.getService().isBlank()) {
            service = serviceRepository.findBySlug(dto.getService()).orElse(null);
        }

        OrderEntity order = OrderEntity.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .service(service)
                .comment(dto.getComment())
                .status("NEW")
                .user(user)
                .build();

        OrderEntity saved = orderRepository.save(order);

        // ===== Отправка писем (не критично для сохранения заявки) =====
        try {
            emailService.sendNewOrderNotification(saved);
            emailService.sendClientConfirmation(saved);
        } catch (Exception e) {
            System.err.println("⚠ Ошибка отправки письма: " + e.getMessage());
        }

        return saved;
    }

    // Все заявки — свежие сверху. С подгрузкой услуги и пользователя.

    @Transactional(readOnly = true)
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAllWithDetailsOrderByCreatedAtDesc();
    }

    // Заявки с указанным статусом. С подгрузкой услуги.

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByStatus(String status) {
        return orderRepository.findByStatusWithDetails(status);
    }

    // Заявки конкретного пользователя (для личного кабинета). С подгрузкой услуги.

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByUser(UserEntity user) {
        return orderRepository.findByUserWithDetails(user);
    }

    // Количество новых заявок — для бейджа в админке.

    @Transactional(readOnly = true)
    public long countNewOrders() {
        return orderRepository.countByStatus("NEW");
    }

    // Смена статуса заявки.

    @Transactional
    public void updateStatus(Long id, String status) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена: " + id));
        order.setStatus(status);
    }

    // Удаление заявки.

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}