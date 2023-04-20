package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @NotBlank
    @Size(min = 8, message = "password cannot be shorter, than 8 characters")
    String oldPassword;
    @NotBlank
    @Size(min = 8, message = "password cannot be shorter, than 8 characters")
    String newPassword;
}
