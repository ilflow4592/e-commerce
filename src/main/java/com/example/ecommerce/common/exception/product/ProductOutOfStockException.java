package com.example.ecommerce.common.exception.product;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductOutOfStockException extends RuntimeException{
    private final HttpStatus status;

    public ProductOutOfStockException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
