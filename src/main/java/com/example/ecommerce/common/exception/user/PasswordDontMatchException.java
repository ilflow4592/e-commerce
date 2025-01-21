package com.example.ecommerce.common.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PasswordDontMatchException extends RuntimeException {
    private final HttpStatus status;

    public PasswordDontMatchException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
