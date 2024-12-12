package com.example.ecommerce.dto.cart;

public record AddToCartDto(
        Long userId,
        Long productId,
        Integer quantity
) {

}