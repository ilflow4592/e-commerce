package com.example.ecommerce.dto.order;

import lombok.Builder;

import java.util.HashMap;

@Builder
public record CreateOrderDto(
        Long userId,
        Integer totalPrice,
        HashMap<Long, Integer> productsMap // <productId : quantity>
) {

}
