package com.example.ecommerce.controller;

import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
@ResponseBody
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Long> register(@RequestBody RegisterUserDto dto){
        Long userId = userService.register(dto);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }
}
