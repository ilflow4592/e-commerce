package com.example.ecommerce.dto.order;


import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderDto(
        User user,
        Integer totalPrice,
        List<OrderItem> orderItems,
        OrderStatus orderStatus
) {

}
