package com.example.ecommerce.common.exception.review;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReviewAlreadyExistsException extends RuntimeException{
    private final HttpStatus status;

    public ReviewAlreadyExistsException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
