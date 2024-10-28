package com.example.ecommerce.common.exception.order_item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderItemException {

    NOTFOUND("주문하신 상품 정보를 찾을 수 없습니다.",HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
