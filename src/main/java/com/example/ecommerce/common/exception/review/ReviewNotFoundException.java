package com.example.ecommerce.common.exception.review;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReviewNotFoundException extends RuntimeException{
    private final HttpStatus status;

    public ReviewNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
