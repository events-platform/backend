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
    @NotBlank
    String name;
    String location;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    Date beginDate;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    Date endDate;
    @NotNull
    EFormat format;
    @NotNull
    EType type;
    Integer registrationLimit;
    String email;
    String externalLink;
    String description;
    MultipartFile file;
}
