package com.example.eventsplatformbackend.mapper;

import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(target = "image", ignore = true)
    Post postCreationDtoToPost(PostCreationDto postCreationDto);
}
