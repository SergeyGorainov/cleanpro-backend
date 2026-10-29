package ru.cleanpro.cleanprobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {

    @NotBlank(message = "Укажите имя")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    @NotBlank(message = "Укажите телефон")
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{10,20}$",
            message = "Некорректный номер телефона"
    )
    private String phone;

    @Size(max = 255)
    private String address;

    private String service;

    @Size(max = 1000)
    private String comment;
}