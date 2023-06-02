package com.example.eventsplatformbackend.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class UserDto {
    final String username;
    final String about;
    final String email;
    final String phone;
    final String avatar;
}
