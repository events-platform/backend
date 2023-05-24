package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.adapter.objectstorage.S3Adapter;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PostMapper {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public S3Adapter s3Adapter;
    @Value("${aws.default-post-avatar-dir}")
    public String defaultImageDir;
    @Mapping(target = "image", expression = "java(s3Adapter.getLink(defaultImageDir))")
    public abstract Post postCreationDtoToPost(PostCreationDto postCreationDto);
    @Mapping(target = "ownerName", expression = "java(post.getOwner().getUsername())")
    @Mapping(target = "ownerAvatar", expression = "java(post.getOwner().getAvatar())")
    public abstract PostResponseDto postDtoFromPost(Post post);
}
