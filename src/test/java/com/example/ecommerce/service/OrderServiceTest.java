package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderDto createOrderDto;
    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        //userId가 1L이라고 가정한 주문한 상품 2개의 총 가격은 = 100,000 (개당 50,000)
        createOrderDto = new CreateOrderDto(1L, 100000, Map.of(1L, 2));

        user = new User("ILYA", "test123@gmail.com","1234","01012341234");
        user.setId(1L);

        product = new Product("패딩 점퍼", "방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.", 50000, 10);
        product.setId(1L);

        //50,000*2=100,000
        order = Order.builder()
                .user(user)
                .totalPrice(100000)
                .orderItems(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("유저는 주문을 할 수 있다.")
    void createOrder() {
        // Arrange
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Long orderId = orderService.createOrder(createOrderDto);

        // Assert
        assertEquals(order.getId(), orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 사용자를 찾을 수 없으면 UserNotFoundException 예외를 던진다.")
    void createOrderUserNotFound() {
        // Arrange
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 상품을 찾을 수 없으면 ProductNotFoundException 예외를 던진다.")
    void createOrderProductNotFound() {
        // Arrange
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 총 가격이 맞지 않다면 OrderTotalPriceNotCorrectException 예외를 던진다.")
    void createOrderTotalPriceNotCorrect() {
        // Arrange
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        //맞지 않는 총 가격
        CreateOrderDto incorrectDto = new CreateOrderDto(1L, 900, Map.of(1L, 2));

        // Act & Assert
        assertThrows(OrderTotalPriceNotCorrectException.class, () -> orderService.createOrder(incorrectDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 상품의 재고가 없으면 ProductOutOfStockException 예외를 던진다.")
    void createOrderProductOutOfStock() {
        // Arrange
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        System.out.println("product = " + product.getStockQuantity());

        product.updateStockQuantity(1);
        System.out.println("product = " + product.getStockQuantity());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(ProductOutOfStockException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }
}

