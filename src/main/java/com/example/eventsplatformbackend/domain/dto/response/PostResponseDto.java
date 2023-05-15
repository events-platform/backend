package com.example.eventsplatformbackend.domain.dto.response;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PostResponseDto {
    Long id;
    String name;
    EFormat format;
    EType type;
    Integer registrationLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate beginDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate endDate;
    String location;
    String description;
    String email;
    String externalLink;
    String image;
    String ownerName;
}
