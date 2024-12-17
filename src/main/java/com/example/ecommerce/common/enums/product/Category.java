package com.example.ecommerce.common.enums.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    PANTS("바지"),
    TOPS("상의"),
    OUTER("아우터"),
    SHOES("신발"),
    ACCESSORY("액세서리");

    private final String category;

}