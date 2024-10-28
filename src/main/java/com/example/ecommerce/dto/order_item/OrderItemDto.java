package com.example.ecommerce.dto.order_item;

import lombok.Builder;

@Builder
public record OrderItemDto(
        Long id,
        Integer price,
        Integer quantity,
        Long orderId,
        Long productId
) {
}
