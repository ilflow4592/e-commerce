package com.example.ecommerce.service;

import com.example.ecommerce.dto.user.SignUpDto;

public interface UserService {
    Long signUp(SignUpDto dto);
}
