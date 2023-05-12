package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @Size(min = 8, message = "Старый пароль не может быть короче 8 символов")
    String oldPassword;
    @Size(min = 8, message = "Новый пароль не может быть короче 8 символов")
    String newPassword;
}
