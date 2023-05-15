package com.example.eventsplatformbackend.domain.dto.request;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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
    EFormat format;
    @NotNull(message = "Выберите тип мероприятия")
    EType type;
    Integer registrationLimit;
    String email;
    String externalLink;
    String description;
    MultipartFile file;
}
