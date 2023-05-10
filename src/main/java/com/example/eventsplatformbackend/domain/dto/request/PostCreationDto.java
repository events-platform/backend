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

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationDto {
    @NotBlank(message = "Мероприятие должно иметь название")
    String name;
    String location;
    @NotNull(message = "Дата начала не может быть пустой")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    Date beginDate;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    Date endDate;
    @NotNull
    EFormat format;
    @NotNull(message = "Тип мероприятия не может быть пустым")
    EType type;
    Integer registrationLimit;
    String email;
    String externalLink;
    String description;
    MultipartFile file;
}
