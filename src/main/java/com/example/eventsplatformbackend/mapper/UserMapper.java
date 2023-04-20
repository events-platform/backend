package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.UserEditDto;
import com.example.eventsplatformbackend.domain.entity.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {
    @Value("${server.default-avatar-dir}")
    private String defaultAvatarDir;
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFromUserEditDto(UserEditDto dto, @MappingTarget User entity);
    @Mapping(target = "avatar", expression = "java(this.getDefaultAvatarDir())")
    public abstract User registrationDtoToUser(RegistrationDto registrationDto);

    public String getDefaultAvatarDir() {
        return defaultAvatarDir;
    }
}
