package com.example.ecommerce.common.enums.product;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Category {
    PANTS("바지"),
    TOPS("상의"),
    OUTER("아우터"),
    SHOES("신발"),
    ACCESSORY("액세서리");

    private final String category;

    @JsonValue
    public String getCategory() {
        return category;
    }

}