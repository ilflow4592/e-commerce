package com.example.ecommerce.controller;

import com.example.ecommerce.dto.user.SignUpDto;
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
    public ResponseEntity<Long> signUp(@RequestBody SignUpDto dto){
        Long userId = userService.signUp(dto);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }
}
