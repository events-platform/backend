package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.dto.UserDto;
import com.example.eventsplatformbackend.dto.UserCreationDto;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.model.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {
    public static User creationDtoToUser(UserCreationDto userCreationDto){
        UserCredentials userCredentials = UserCredentials.builder()
                .email(userCreationDto.getEmail())
                .password(userCreationDto.getPassword())
                .build();

        User user = User.builder()
                .username(userCreationDto.getUsername())
                .build();

        userCredentials.setUser(user);
        user.setUserCredentials(userCredentials);

        return user;
    }

    public static UserDto userToUserDto(User user){
        UserCredentials userCredentials = user.getUserCredentials();

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(userCredentials.getEmail())
                .password(userCredentials.getPassword())
                .build();
    }
}
