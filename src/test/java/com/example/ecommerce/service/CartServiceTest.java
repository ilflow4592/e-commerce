package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.cart.UpdateCartProductDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    /**
     * 모킹 객체
     */
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private CartServiceImpl cartService;

    /**
     * 실제 엔티티
     */
    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("ILYA")
                .email("test@naver.com")
                .password("1234")
                .phoneNumber("01012341234")
                .build();

        product = Product.builder()
                .id(1L)
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .build();

        cart = Cart.builder()
                .user(user)
                .cartHasProducts(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시, 해당하는 상품이 존재하지 않으면, ProductNotFound 예외를 던진다.")
    void addProductToCart_throw_NotFound(){
        // given
        UpdateCartProductDto updateCartProductDto = UpdateCartProductDto.builder()
                .userId(1L)
                .productId(2L)
                .quantity(100)
                .build();

        // when, then
        assertThrows(ProductNotFoundException.class, () -> cartService.updateCartProductQuantity(updateCartProductDto));
    }

    @Test
    @DisplayName("상품 재고 부족 시 ProductOutOfStockException 예외를 던진다.")
    void addProductToCart_throw_OutOfStock() {
        // given
        UpdateCartProductDto updateCartProductDto = UpdateCartProductDto.builder()
                .userId(1L)
                .productId(1L)
                //실제 재고 = 100
                .quantity(101)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when, then
        assertThrows(ProductOutOfStockException.class, () -> cartService.updateCartProductQuantity(updateCartProductDto));
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시, 사용자가 존재하지 않으면, UserNotFound 예외를 던진다.")
    void addProductToCart_throw_UserNotFound() {
        // given
        UpdateCartProductDto updateCartProductDto = UpdateCartProductDto.builder()
                .userId(2L)
                .productId(1L)
                .quantity(100)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findById(updateCartProductDto.userId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class, () -> cartService.updateCartProductQuantity(updateCartProductDto));
    }


    @Test
    @DisplayName("장바구니에 상품을 추가하려고 할 때, 장바구니가 존재하지 않을 경우, 장바구니를 생성한다")
    void makeCartIfNotExist(){
        // given
        UpdateCartProductDto updateCartProductDto = UpdateCartProductDto.builder()
                .userId(1L)
                .productId(1L)
                .quantity(100)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        cartService.updateCartProductQuantity(updateCartProductDto);

        // then
        verify(cartRepository, times(1)).save(any(Cart.class));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .cartHasProducts(new ArrayList<>())
                        .build());
        assertNotNull(cart);
    }

    @Test
    @DisplayName("장바구니에 상품을 추가할 수 있다.")
    void addProductToCart() {
        // given
        UpdateCartProductDto updateCartProductDto = UpdateCartProductDto.builder()
                .userId(1L)
                .productId(1L)
                .quantity(1)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // when
        cartService.updateCartProductQuantity(updateCartProductDto);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByUser(user);
        verify(cartRepository, times(1)).save(cart);

        assertEquals(1, cart.getCartHasProducts().size());
        CartHasProduct cartHasProduct = cart.getCartHasProducts().get(0);
        assertEquals(product, cartHasProduct.getProduct());
        assertEquals(1, cartHasProduct.getQuantity());
    }
}

