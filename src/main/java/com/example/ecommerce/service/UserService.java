package com.example.ecommerce.service;

import com.example.ecommerce.dto.SignUpDto;

public interface UserService {
    Long signUp(SignUpDto dto);
}
