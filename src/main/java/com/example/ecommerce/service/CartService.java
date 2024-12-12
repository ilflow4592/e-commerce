package com.example.ecommerce.service;


import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;

public interface CartService {
    Long addProduct(AddToCartDto dto);
    Long removeProduct(RemoveFromCartDto dto);
}
