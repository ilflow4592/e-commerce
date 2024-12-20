package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;
import com.example.ecommerce.dto.cart.UpdateCartProductDto;
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
    public void updateCartProductQuantity(UpdateCartProductDto dto) {
        Product product = findProductById(dto.productId());
        validateStockAvailability(product, dto.quantity());
        User user = findUserById(dto.userId());
        Cart cart = findOrCreateCart(user);

        updateCartWithProduct(cart, product, dto.quantity());

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeProduct(RemoveFromCartDto dto) {
        findProductById(dto.productId());
        User user = findUserById(dto.userId());
        Cart cart = findOrCreateCart(user);
        cart.removeProduct(dto.productId());
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        ProductException.NOTFOUND.getStatus(),
                        ProductException.NOTFOUND.getMessage()
                ));
    }

    private void validateStockAvailability(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new ProductOutOfStockException(
                    ProductException.OUT_OF_STOCK.getStatus(),
                    ProductException.OUT_OF_STOCK.getMessage()
            );
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        UserException.NOTFOUND.getStatus(),
                        UserException.NOTFOUND.getMessage()
                ));
    }

    private Cart findOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .cartHasProducts(new ArrayList<>())
                        .build());
    }

    private void updateCartWithProduct(Cart cart, Product product, Integer quantity) {
        List<CartHasProduct> cartHasProducts = cart.getCartHasProducts();

        int index = getProductIndex(cartHasProducts, product.getId());

        if (index != -1) {
            cartHasProducts.get(index).updateQuantityWithTotalPrice(quantity, product.getUnitPrice());
        } else {
            cart.addProduct(product, quantity);
        }
    }

    private int getProductIndex(List<CartHasProduct> cartHasProducts, Long productId) {
        return IntStream.range(0, cartHasProducts.size())
                .filter(i -> {
                    Product product = cartHasProducts.get(i).getProduct();
                    return product != null && product.getId().equals(productId);
                })
                .findFirst()
                .orElse(-1); // 찾는 상품이 없을 경우 -1을 반환
    }
}
