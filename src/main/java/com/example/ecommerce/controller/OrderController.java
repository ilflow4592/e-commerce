package com.example.ecommerce.controller;

import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderDto createOrderDto){
        Long orderId = orderService.createOrder(createOrderDto);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
