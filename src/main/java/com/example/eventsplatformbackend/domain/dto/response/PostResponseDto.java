package com.example.eventsplatformbackend.domain.dto.response;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponseDto {
    Long id;
    String name;
    EFormat format;
    EType type;
    Integer registrationLimit;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime beginDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;
    String location;
    String description;
    String email;
    String formLink;
    String externalLink;
    String image;
    String ownerName;
    String ownerAvatar;
}
