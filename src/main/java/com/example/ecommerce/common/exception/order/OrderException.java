package com.example.ecommerce.common.exception.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderException {

    NOT_CORRECT("총 가격이 알맞지 않습니다.", HttpStatus.BAD_REQUEST),
    NOTFOUND("주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
