package com.example.ecommerce.service;

import com.example.ecommerce.dto.user.LoginUserDto;
import com.example.ecommerce.dto.user.RegisterUserDto;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    Long register(RegisterUserDto registerUserDto);

    Object login(LoginUserDto loginUserDto, HttpSession session);
}
