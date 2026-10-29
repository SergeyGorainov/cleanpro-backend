package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> buildDashboardStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        Map<String, Object> stats = new LinkedHashMap<>();

        // Общие счётчики
        stats.put("totalOrders", orderRepository.count());
        stats.put("newOrders", orderRepository.countByStatus("NEW"));
        stats.put("inProgressOrders", orderRepository.countByStatus("IN_PROGRESS"));
        stats.put("doneOrders", orderRepository.countByStatus("DONE"));

        // За период
        stats.put("todayOrders", orderRepository.countByCreatedAtAfter(todayStart));
        stats.put("weekOrders", orderRepository.countByCreatedAtAfter(weekStart));
        stats.put("monthOrders", orderRepository.countByCreatedAtAfter(monthStart));

        // Последние 5 заявок — с подгрузкой услуги и пользователя
        List<OrderEntity> last5 = orderRepository.findAllWithDetailsOrderByCreatedAtDesc()
                .stream().limit(5).toList();
        stats.put("lastOrders", last5);

        // График за 7 дней
        List<Object[]> rawByDay = orderRepository.countOrdersByDay(weekStart);
        Map<String, Long> byDay = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            byDay.put(LocalDate.now().minusDays(i).toString(), 0L);
        }
        for (Object[] row : rawByDay) {
            byDay.put((String) row[0], ((Number) row[1]).longValue());
        }
        stats.put("chartLabels", new ArrayList<>(byDay.keySet()));
        stats.put("chartValues", new ArrayList<>(byDay.values()));

        return stats;
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getAllOrdersForExport() {
        return orderRepository.findAllWithDetailsOrderByCreatedAtDesc();
    }
}