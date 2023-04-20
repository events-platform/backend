package com.example.eventsplatformbackend.domain.dto.request;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEditDto {
    String username;
    String about;
    String email;
    String phone;
}
