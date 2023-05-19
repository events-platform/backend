package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostIdDto {
    @NotNull
    Long postId;
}
