package com.example.eventsplatformbackend.domain.dto.request;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostSearchDto {

    LocalDateTime beginDate;

    LocalDateTime endDate;

    List<String> organizer;

    List<String> type;

    List<String> format;

    Boolean showEnded = false;

    String searchQuery;

    Pageable pageable;

}
