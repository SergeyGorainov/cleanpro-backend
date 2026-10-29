package ru.cleanpro.cleanprobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDto {

    @NotBlank(message = "Укажите текущий пароль")
    private String currentPassword;

    @NotBlank(message = "Укажите новый пароль")
    @Size(min = 6, message = "Пароль должен быть не короче 6 символов")
    private String newPassword;

    @NotBlank(message = "Повторите новый пароль")
    private String confirmPassword;
}