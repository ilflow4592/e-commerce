package com.example.ecommerce.dto;

import com.example.ecommerce.entity.User;
import lombok.Builder;

@Builder
public record SignUpDto(
        Long id,
        String name,
        String email,
        String password,
        String phoneNumber) {

    public static User toEntity(UserDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .build();
    }

}