package com.example.ecommerce.common.exception.order;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderNotFoundException extends RuntimeException{
    private final HttpStatus status;

    public OrderNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
