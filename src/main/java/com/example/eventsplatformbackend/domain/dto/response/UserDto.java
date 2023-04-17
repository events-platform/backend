package com.example.eventsplatformbackend.domain.dto.response;

import com.example.eventsplatformbackend.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UserDto {
    final String username;
    final String about;
    final String email;
    final String avatar;

    public UserDto(User user){
        this.username = user.getUsername();
        this.about = user.getAbout();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
    }
}
