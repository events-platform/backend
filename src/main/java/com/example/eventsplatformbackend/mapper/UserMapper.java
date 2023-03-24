package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.dto.UserDto;
import com.example.eventsplatformbackend.dto.UserCreationDto;
import com.example.eventsplatformbackend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {
    public static User creationDtoToUser(UserCreationDto userCreationDto){

        return User.builder()
                .username(userCreationDto.getUsername())
                .password(userCreationDto.getPassword())
                .build();
    }

    public static UserDto userToUserDto(User user){

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
