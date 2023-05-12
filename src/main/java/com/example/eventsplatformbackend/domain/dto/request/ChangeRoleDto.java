package com.example.eventsplatformbackend.domain.dto.request;

import com.example.eventsplatformbackend.domain.enumeration.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String username;
    @NotNull(message = "Назначаемая роль не может быть пустой")
    ERole role;
}
