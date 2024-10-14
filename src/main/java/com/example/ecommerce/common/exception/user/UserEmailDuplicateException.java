package com.example.ecommerce.common.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserEmailDuplicateException extends RuntimeException {
    private final HttpStatus status;

    public UserEmailDuplicateException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}

