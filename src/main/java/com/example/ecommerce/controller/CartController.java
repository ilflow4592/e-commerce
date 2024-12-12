package com.example.ecommerce.controller;

import com.example.ecommerce.dto.cart.CartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items/add")
    public ResponseEntity<Long> addProduct(@RequestBody CartDto dto) {
        Long cartId = cartService.addProduct(dto);
        return ResponseEntity.ok(cartId);
    }

    //TODO : quantity up & down update.
}
