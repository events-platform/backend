package com.example.eventsplatformbackend.domain.dto.request;

import com.example.eventsplatformbackend.domain.entity.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDto {
    @NotBlank
    String username;
    @NotNull
    ERole role;
}
