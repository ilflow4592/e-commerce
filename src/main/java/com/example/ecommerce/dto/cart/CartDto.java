package com.example.ecommerce.dto.cart;

public record CartDto(
        Long userId,
        Long productId,
        Integer quantity
) {

}