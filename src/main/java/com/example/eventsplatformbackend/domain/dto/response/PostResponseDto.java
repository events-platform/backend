package com.example.eventsplatformbackend.domain.dto.response;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class PostResponseDto {
    Long id;
    @NotBlank
    @Column(name = "name")
    String name;
    @NotNull
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    EFormat format;
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    EType type;
    @Column(name = "registration_limit")
    Integer registrationLimit;
    @Column(name = "begin_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date beginDate;
    @Column(name = "end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date endDate;
    @Column(name = "location")
    String location;
    @Column(name = "description")
    String description;
    @Column(name = "email")
    String email;
    @Column(name = "link")
    String externalLink;
    @Column(name = "image")
    String image;
}
