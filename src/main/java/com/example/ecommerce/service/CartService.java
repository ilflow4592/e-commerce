package com.example.ecommerce.service;


import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;

public interface CartService {
    void addProduct(AddToCartDto dto);
    void removeProduct(RemoveFromCartDto dto);
}
