package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.cart.CartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartHasProduct;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService{

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Long addProduct(CartDto dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(ProductException.NOTFOUND.getStatus(), ProductException.NOTFOUND.getMessage()));

        //상품 재고 체크
        if ((product.getStockQuantity() - dto.quantity()) <= 0) {
            throw new ProductOutOfStockException(
                    ProductException.OUT_OF_STOCK.getStatus(),
                    ProductException.OUT_OF_STOCK.getMessage()
            );
        }

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(UserException.NOTFOUND.getStatus(), UserException.NOTFOUND.getMessage()));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .cartHasProducts(new ArrayList<>())
                        .build());

        List<CartHasProduct> cartHasProducts = cart.getCartHasProducts();
        //상품이 cartHasProducts 내부에 존재하는지 체크
        int index = getProductIndex(cartHasProducts, product.getId());

        //있을 때 -> 기존 상품의 수량과 가격을 업데이트
        if(index != -1){
            cartHasProducts.get(index).updateCartHasProduct(dto.quantity(), product.getUnitPrice());
        }else{
            //없을 때 -> 새로운 상품을 추가
            cart.addProduct(product, dto.quantity());
        }

        cartRepository.save(cart);

        return cart.getId();
    }

    private int getProductIndex(List<CartHasProduct> cartHasProducts, Long productId) {
        return IntStream.range(0, cartHasProducts.size())
                .filter(i -> {
                    Product product = cartHasProducts.get(i).getProduct();
                    return product != null && product.getId().equals(productId);
                })
                .findFirst()
                .orElse(-1); // 조건에 맞는 인덱스가 없으면 -1 반환
    }
}
