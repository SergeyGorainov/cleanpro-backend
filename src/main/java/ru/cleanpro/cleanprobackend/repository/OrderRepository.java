package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Все заявки с подгрузкой услуги и пользователя — для админки
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.service " +
            "LEFT JOIN FETCH o.user " +
            "ORDER BY o.createdAt DESC")
    List<OrderEntity> findAllWithDetailsOrderByCreatedAtDesc();

    // Заявки по статусу с подгрузкой услуги
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.service " +
            "WHERE o.status = :status " +
            "ORDER BY o.createdAt DESC")
    List<OrderEntity> findByStatusWithDetails(@Param("status") String status);

    // Заявки конкретного пользователя с подгрузкой услуги
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.service " +
            "WHERE o.user = :user " +
            "ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserWithDetails(@Param("user") UserEntity user);

    // Простые запросы без JOIN FETCH — для счётчиков дашборда
    List<OrderEntity> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    List<OrderEntity> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime from);

    long countByCreatedAtAfter(LocalDateTime from);

    long countByStatusAndCreatedAtAfter(String status, LocalDateTime from);

    @Query("""
            SELECT FUNCTION('to_char', o.createdAt, 'YYYY-MM-DD') AS day, COUNT(o)
            FROM OrderEntity o
            WHERE o.createdAt >= :from
            GROUP BY FUNCTION('to_char', o.createdAt, 'YYYY-MM-DD')
            ORDER BY day ASC
            """)
    List<Object[]> countOrdersByDay(@Param("from") LocalDateTime from);
}