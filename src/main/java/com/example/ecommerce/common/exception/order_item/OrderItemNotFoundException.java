package com.example.ecommerce.common.exception.order_item;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderItemNotFoundException extends RuntimeException{
    private final HttpStatus status;

    public OrderItemNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
