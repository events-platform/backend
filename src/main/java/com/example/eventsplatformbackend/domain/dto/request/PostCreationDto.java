package com.example.eventsplatformbackend.domain.dto.request;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationDto {
    @NotBlank(message = "Введите название мероприятия")
    String name;
    String location;
    @NotNull(message = "Введите дату начала мероприятия")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime beginDate;
    @NotNull(message = "Введите дату окончания мероприятия")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime endDate;
    @NotNull(message = "Выберите формат мероприятия")
    @Schema(description = "Case sensitive", example = "ОНЛАЙН")
    EFormat format;
    @NotNull(message = "Выберите тип мероприятия")
    @Schema(description = "Case sensitive", example = "МИТАП")
    EType type;
    @NotNull(message = "Введите количество мест для регистрации")
    Integer registrationLimit;
    String formLink;
    String email;
    String externalLink;
    String description;
}
