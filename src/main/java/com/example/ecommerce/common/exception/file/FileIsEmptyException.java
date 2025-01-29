package com.example.ecommerce.common.exception.file;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileIsEmptyException extends RuntimeException {

    private final HttpStatus status;

    public FileIsEmptyException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
