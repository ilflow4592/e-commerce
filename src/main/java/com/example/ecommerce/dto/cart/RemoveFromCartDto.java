package com.example.ecommerce.dto.cart;

public record RemoveFromCartDto(
        Long userId,
        Long productId
) {

}