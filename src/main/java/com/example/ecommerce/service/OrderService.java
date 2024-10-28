package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.CreateOrderDto;

public interface OrderService {
    Long createOrder(CreateOrderDto createOrderDto);
}
