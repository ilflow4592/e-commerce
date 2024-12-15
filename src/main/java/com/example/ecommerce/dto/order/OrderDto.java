package com.example.ecommerce.dto.order;


import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.dto.user.UserDto;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderDto(
        @JsonProperty("user")
        UserDto userDto,
        Integer totalPrice,
        List<OrderItem> orderItems,
        OrderStatus orderStatus
) {

}
