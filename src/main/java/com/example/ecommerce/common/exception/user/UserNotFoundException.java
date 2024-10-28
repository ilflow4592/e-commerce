package com.example.ecommerce.common.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final HttpStatus status;

    public UserNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
