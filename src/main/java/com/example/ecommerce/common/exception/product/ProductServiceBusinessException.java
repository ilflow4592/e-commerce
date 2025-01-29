package com.example.ecommerce.common.exception.product;

import lombok.Getter;


@Getter
public class ProductServiceBusinessException extends RuntimeException {

    public ProductServiceBusinessException(String message) {
        super(message);
    }

}

