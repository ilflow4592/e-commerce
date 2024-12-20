package com.example.ecommerce.dto.cart;

import lombok.Builder;

@Builder
public record RemoveFromCartDto(
        Long userId,
        Long productId
) {

}