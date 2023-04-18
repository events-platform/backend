package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.entity.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class UserMapperImpl{
    @Value("${server.default-avatar-dir}")
    private String defaultAvatarsDirectory;
    public User registrationDtoToUser(RegistrationDto registrationDto){
        return User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .avatar(defaultAvatarsDirectory)
                .build();
    }
}
