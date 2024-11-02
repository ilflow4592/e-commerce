package com.example.ecommerce.common.exception.port_one;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PortOneNotFoundPaymentException extends RuntimeException{

    private final HttpStatus status;

    public PortOneNotFoundPaymentException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
