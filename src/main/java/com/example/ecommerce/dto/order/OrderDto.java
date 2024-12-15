package com.example.ecommerce.dto.order;


import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.dto.order_item.OrderItemDto;
import com.example.ecommerce.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderDto(
        @JsonProperty("user")
        UserDto userDto,
        Integer totalPrice,
        @JsonProperty("orderedItems")
        List<OrderItemDto> orderItemsDto,
        OrderStatus orderStatus
) {

}
