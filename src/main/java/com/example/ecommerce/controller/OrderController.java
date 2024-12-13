package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
import com.example.ecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/verify/{paymentId}")
    public ResponseEntity<Long> verifyPaymentAndCreateOrder(@PathVariable String paymentId, @RequestBody CreateOrderDto createOrderDto){
        Long orderId = orderService.verifyPaymentAndCreateOrder(paymentId, createOrderDto);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageableDto<OrderDto>> getAllOrders(Pageable pageable){
        PageableDto<OrderDto> pageableOrderDto = orderService.getAllOrders(pageable);
        return new ResponseEntity<>(pageableOrderDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id){
        OrderDto orderDto = orderService.getOrder(id);
        return new ResponseEntity<>(orderDto,HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok("id = " + id + "인 주문이 성공적으로 삭제되었습니다.");
    }


}
