package ru.cleanpro.cleanprobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDto {

    private Long id;

    @NotBlank(message = "Укажите имя")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Укажите фамилию")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotBlank(message = "Укажите email")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Укажите роль")
    private String role;   // "USER" или "ADMIN"
}