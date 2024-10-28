package com.example.ecommerce.dto.order;

import lombok.Builder;

import java.util.Map;

@Builder
public record CreateOrderDto(
        Long userId,
        Integer totalPrice,
        Map<Long, Integer> productsMap // <productId : quantity>
) {

}
