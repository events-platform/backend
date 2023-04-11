package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.dto.RegistrationDto;
import com.example.eventsplatformbackend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {
    public static User registrationDtoToUser(RegistrationDto registrationDto){
        return User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .build();
    }
}
