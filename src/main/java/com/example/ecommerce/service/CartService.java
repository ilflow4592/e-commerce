package com.example.ecommerce.service;


import com.example.ecommerce.dto.cart.UpdateCartProductDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;

public interface CartService {
    void updateCartProductQuantity(UpdateCartProductDto dto);
    void removeProduct(RemoveFromCartDto dto);
}
