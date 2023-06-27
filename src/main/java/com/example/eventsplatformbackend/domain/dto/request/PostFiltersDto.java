package com.example.eventsplatformbackend.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostFiltersDto {
    LocalDateTime beginDateFilter;
    LocalDateTime endDateFilter;
    List<String> organizers;
    List<String> types;
    List<String> formats;
    Boolean showEndedPosts;
    String searchQuery;
    Pageable pageable;
}
