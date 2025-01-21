package com.example.ecommerce.service;

import com.example.ecommerce.dto.user.RegisterUserDto;

public interface UserService {
    Long register(RegisterUserDto registerUserDto);
}
