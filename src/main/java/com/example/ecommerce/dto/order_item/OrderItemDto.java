package com.example.ecommerce.dto.order_item;

import com.example.ecommerce.entity.OrderItem;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderItemDto(
        Long orderItemId,
        Integer price,
        Integer quantity,
        Long orderId,
        Long productId
) {

    public static List<OrderItemDto> toDto(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemDto.builder()
                        .orderItemId(orderItem.getId())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .orderId(orderItem.getOrder().getId())
                        .productId(orderItem.getProduct().getId())
                        .build())
                .toList();
    }
}
