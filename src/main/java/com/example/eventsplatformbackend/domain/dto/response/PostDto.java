package com.example.eventsplatformbackend.domain.dto.response;

import com.example.eventsplatformbackend.domain.entity.EFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PostDto {
    String name;
    EFormat format;
    String city;
    Integer registrationLimit;
    Date beginDate;
    Date endDate;
    String location;
    String description;
    String image;
}
