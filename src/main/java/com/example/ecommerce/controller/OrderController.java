package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
import com.example.ecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderDto createOrderDto){
        Long orderId = orderService.createOrder(createOrderDto);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageableDto<OrderDto>> getAllOrders(Pageable pageable){
        PageableDto<OrderDto> pageableOrderDto = orderService.getAllOrders(pageable);
        return new ResponseEntity<>(pageableOrderDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getAllOrders(@PathVariable Long id){
        OrderDto orderDto = orderService.getOrder(id);
        return new ResponseEntity<>(orderDto,HttpStatus.OK);


    }




}
