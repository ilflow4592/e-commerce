package com.example.ecommerce.controller;

import com.example.ecommerce.dto.cart.UpdateCartProductDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/update-qty")
    public ResponseEntity<String> updateCartProductQuantity(@RequestBody UpdateCartProductDto dto) {
        cartService.updateCartProductQuantity(dto);
        return new ResponseEntity<>("상품이 성공적으로 등록되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("/delete-product")
    public ResponseEntity<String> removeProduct(@RequestBody RemoveFromCartDto dto) {
        cartService.removeProduct(dto);
        return new ResponseEntity<>("상품이 성공적으로 제거되었습니다.", HttpStatus.OK);
    }


}
