package ru.cleanpro.cleanprobackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Категория — например, "himchistka", "cleaning". Можно использовать для группировки
    @Column(length = 100)
    private String category;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 3000)
    private String answer;

    // Порядок отображения — меньше, значит выше
    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    public void prePersist() {
        if (active == null) active = true;
        if (sortOrder == null) sortOrder = 100;
    }
}