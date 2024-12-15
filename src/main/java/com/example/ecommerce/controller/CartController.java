package com.example.ecommerce.controller;

import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/products/add")
    public ResponseEntity<String> addProduct(@RequestBody AddToCartDto dto) {
        cartService.addProduct(dto);
        return new ResponseEntity<>("상품이 성공적으로 등록되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/products/remove")
    public ResponseEntity<String> removeProduct(@RequestBody RemoveFromCartDto dto) {
        cartService.removeProduct(dto);
        return new ResponseEntity<>("상품이 성공적으로 제거되었습니다.", HttpStatus.OK);
    }


}
