package com.example.eventsplatformbackend.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class UserDto {
    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
    String password;
}
