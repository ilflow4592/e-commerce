package com.example.ecommerce.dto;

import com.example.ecommerce.entity.User;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String name,
        String email,
        String phoneNumber) {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }


    public static User toEntity(SignUpDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .phoneNumber(dto.phoneNumber())
                .build();
    }

}