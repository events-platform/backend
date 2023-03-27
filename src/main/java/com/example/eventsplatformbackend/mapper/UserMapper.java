package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {
    public static User creationDtoToUser(LoginDto loginDto){
        return User.builder()
                .username(loginDto.getUsername())
                .password(loginDto.getPassword())
                .build();
    }
}
