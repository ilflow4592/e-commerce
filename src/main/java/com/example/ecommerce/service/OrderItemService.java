package com.example.ecommerce.service;

import com.example.ecommerce.dto.order_item.OrderItemDto;

public interface OrderItemService {
    OrderItemDto getOrderItem(Long id);
}
