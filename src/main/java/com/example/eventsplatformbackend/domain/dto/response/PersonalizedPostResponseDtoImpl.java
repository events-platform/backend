package com.example.eventsplatformbackend.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PersonalizedPostResponseDtoImpl extends PostResponseDto {
    Boolean isSubscribed;
    Boolean isFavorite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonalizedPostResponseDtoImpl that = (PersonalizedPostResponseDtoImpl) o;
        return Objects.equals(isSubscribed, that.isSubscribed) && Objects.equals(isFavorite, that.isFavorite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isSubscribed, isFavorite);
    }
}