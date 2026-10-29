package ru.cleanpro.cleanprobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @NotBlank(message = "Укажите имя")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Укажите фамилию")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotBlank(message = "Укажите email")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Укажите пароль")
    @Size(min = 6, message = "Пароль должен быть не короче 6 символов")
    private String password;

    @NotBlank(message = "Повторите пароль")
    private String confirmPassword;
}