package ru.cleanpro.cleanprobackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "calculator_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculatorPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Уникальный ключ параметра:
    //  service_regular, service_general, service_renovation — цена за м² для услуги
    //  type_flat, type_house, type_office — множитель типа объекта
    //  room_1, room_2, ..., room_5 — доп. за комнаты
    //  extra_windows, extra_furniture, extra_appliances, extra_balcony — доп. услуги
    //  base_regular, base_general, base_renovation — базовая цена

    @Column(nullable = false, unique = true)
    private String code;

    // Название для отображения в админке
    @Column(nullable = false)
    private String title;

    // Числовое значение (цена или множитель)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    // Порядок отображения в админке
    @Column(nullable = false)
    private Integer sortOrder;

    @PrePersist
    public void prePersist() {
        if (sortOrder == null) sortOrder = 100;
    }
}