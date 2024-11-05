package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService{

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Long addItem(AddToCartDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(UserException.NOTFOUND.getStatus(), UserException.NOTFOUND.getMessage()));
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(ProductException.NOTFOUND.getStatus(), ProductException.NOTFOUND.getMessage()));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .cartItems(new ArrayList<>())
                        .build());

        cart.addItem(product);
        cartRepository.save(cart);

        return cart.getId();
    }
}
