package com.example.ecommerce.controller;

import com.example.ecommerce.dto.order_item.OrderItemDto;
import com.example.ecommerce.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-items")
@AllArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("{id}")
    public ResponseEntity<OrderItemDto> getOrderItem(@PathVariable Long id){
        OrderItemDto orderItem = orderItemService.getOrderItem(id);
        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }
}
