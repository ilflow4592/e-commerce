package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.dto.cart.AddToCartDto;
import com.example.ecommerce.dto.cart.RemoveFromCartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartHasProduct;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User("test_user", "test@example.com", "password", "1234567890");
        user.setId(1L);

        product = new Product("Test Product", "A sample product", 1000, 10);
        product.setId(1L);

        cart = Cart.builder()
                .user(user)
                .cartHasProducts(new ArrayList<>())
                .build();
        cart.setId(1L);
    }

    @Test
    @DisplayName("장바구니에 상품을 추가할 수 있다.")
    void addProductToCart() {
        // given
        AddToCartDto dto = new AddToCartDto(1L, 1L, 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        Long cartId = cartService.addProduct(dto);

        // then
        assertNotNull(cartId);
        assertEquals(1L, cartId);
        verify(productRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("상품 재고 부족 시 ProductOutOfStockException 예외를 던진다.")
    void addProductToCart_OutOfStock() {
        // given
        AddToCartDto dto = new AddToCartDto(1L, 1L, 15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when, then
        assertThrows(ProductOutOfStockException.class, () -> cartService.addProduct(dto));
        verify(productRepository, times(1)).findById(1L);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("장바구니에서 상품을 제거할 수 있다.")
    void removeProductFromCart() {
        // given
        RemoveFromCartDto dto = new RemoveFromCartDto(1L, 1L);

        CartHasProduct cartHasProduct = new CartHasProduct(cart, product, 2, 2000);
        List<CartHasProduct> cartHasProducts = new ArrayList<>();
        cartHasProducts.add(cartHasProduct);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        Long cartId = cartService.removeProduct(dto);

        // then
        assertNotNull(cartId);
        assertEquals(1L, cartId);
        assertTrue(cart.getCartHasProducts().isEmpty());
        verify(productRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("존재하지 않는 상품 제거 시 장바구니 내용은 유지된다.")
    void removeProductFromCart_NotFound() {
        // given
        RemoveFromCartDto dto = new RemoveFromCartDto(1L, 1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // when
        Long cartId = cartService.removeProduct(dto);

        // then
        assertNotNull(cartId);
        assertEquals(1L, cartId);
        assertTrue(cart.getCartHasProducts().isEmpty());
        verify(productRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByUser(user);
    }
}
