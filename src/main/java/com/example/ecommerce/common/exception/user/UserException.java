package com.example.ecommerce.common.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserException {

    DUPLICATE("동일한 이메일을 소유한 유저가 이미 존재합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}