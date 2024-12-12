package com.example.ecommerce.service;


import com.example.ecommerce.dto.cart.CartDto;

public interface CartService {
    Long addProduct(CartDto dto);
}
