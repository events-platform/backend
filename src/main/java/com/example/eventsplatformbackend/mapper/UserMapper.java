package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.UserEditDto;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.service.objectstorage.S3FileService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public S3FileService s3FileService;
    @Value("${aws.default-avatar-dir}")
    public String defaultAvatarDir;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFromUserEditDto(UserEditDto dto, @MappingTarget User entity);
    @Mapping(target = "avatar", expression = "java(s3FileService.getLink(defaultAvatarDir))")
    public abstract User registrationDtoToUser(RegistrationDto registrationDto);
}
