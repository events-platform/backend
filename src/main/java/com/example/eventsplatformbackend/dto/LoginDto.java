package com.example.eventsplatformbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LoginDto {
    @NotBlank
    String username;
    @NotBlank
    @Size(min = 8)
    String password;
}
