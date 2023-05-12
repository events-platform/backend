package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class RegistrationDto {
    @NotBlank(message = "Введите имя пользователя")
    String username;
    @NotBlank(message = "Введите адрес электронной почты")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Неправильный формат почты")
    String email;
    @Size(min = 8, message = "Пароль не может быть короче 8 символов")
    String password;
}
