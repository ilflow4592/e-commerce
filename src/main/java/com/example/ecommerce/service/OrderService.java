package com.example.ecommerce.service;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Long verifyPaymentAndCreateOrder(String paymentId, CreateOrderDto createOrderDto);
    PageableDto<OrderDto> getAllOrders(Pageable pageable);
    OrderDto getOrder(Long id);
    void deleteOrder(Long id);
}
