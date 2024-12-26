package com.example.ecommerce.common.exception.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewException {

    NOTFOUND("리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS("리뷰를 이미 작성하였습니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;
}
