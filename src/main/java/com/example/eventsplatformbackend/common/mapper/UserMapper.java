package com.example.eventsplatformbackend.common.mapper;

import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.UserEditDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.service.s3.S3ServiceImpl;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public S3ServiceImpl s3Service;
    @Value("${aws.default-avatar-dir}")
    public String defaultAvatarDir;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFromUserEditDto(UserEditDto dto, @MappingTarget User entity);
    @Mapping(target = "avatar", expression = "java(s3Service.pickRandomObject(defaultAvatarDir))")
    public abstract User registrationDtoToUser(RegistrationDto registrationDto);
    public abstract UserDto userToUserDto(User user);
}
