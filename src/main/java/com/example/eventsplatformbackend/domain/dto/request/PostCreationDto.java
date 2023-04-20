package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationDto {
    @NotBlank
    String token;
    @NotBlank
    String name;
    String description;
    @NotBlank
    Date date;
    String imageSource;
}
