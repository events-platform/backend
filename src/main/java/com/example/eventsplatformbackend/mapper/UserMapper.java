package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.domain.dto.request.UserEditDto;
import com.example.eventsplatformbackend.domain.entity.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Value("${server.default-avatar-dir}")
    private final String defaultAvatarsDirectory;

    protected UserMapper(String defaultAvatarsDirectory) {
        this.defaultAvatarsDirectory = defaultAvatarsDirectory;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFromDto(UserEditDto dto, @MappingTarget User entity);
}
