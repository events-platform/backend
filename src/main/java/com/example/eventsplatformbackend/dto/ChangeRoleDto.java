package com.example.eventsplatformbackend.dto;

import com.example.eventsplatformbackend.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDto {
    @NotBlank
    String username;
    @NotNull
    Role role;
}
