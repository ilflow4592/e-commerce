package com.example.ecommerce.service;


import com.example.ecommerce.dto.cart.AddToCartDto;

public interface CartService {
    Long addItem(AddToCartDto addToCartDto);
}
