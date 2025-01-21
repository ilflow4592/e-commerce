package com.example.ecommerce.dto.user;

import com.example.ecommerce.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public record UserDto(
        Long userId,
        @JsonIgnore
        String name,
        String email,
        @JsonIgnore
        String phoneNumber) {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }


    public static User toEntity(RegisterUserDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .phoneNumber(dto.phoneNumber())
                .build();
    }

}