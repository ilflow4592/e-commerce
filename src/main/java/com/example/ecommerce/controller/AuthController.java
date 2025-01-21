package com.example.ecommerce.controller;

import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        Long userId = userService.register(registerUserDto);

        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }
}
