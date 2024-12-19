package com.example.ecommerce.dto.cart;

import lombok.Builder;

@Builder
public record UpdateCartProductDto(
        Long userId,
        Long productId,
        Integer quantity
) {

}