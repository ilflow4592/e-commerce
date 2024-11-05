package com.example.ecommerce.controller;

import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Long> addItem(@RequestBody AddToCartDto addToCartDto) {
        Long cartId = cartService.addItem(addToCartDto);
        return ResponseEntity.ok(cartId);
    }

    //TODO : quantity up & down update.
}
