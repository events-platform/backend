package com.example.eventsplatformbackend.domain.dto.request;

import com.example.eventsplatformbackend.domain.entity.EFormat;
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
    @NotBlank
    String name;
    @NotNull
    EFormat format;
    @NotBlank
    String city;
    Integer registrationLimit;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date beginDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date endDate;
    String location;
    String description;
    MultipartFile file;
}
