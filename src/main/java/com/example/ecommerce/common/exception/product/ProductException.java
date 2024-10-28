package com.example.ecommerce.common.exception.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductException {

    NOTFOUND("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK("상품의 재고가 부족합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
