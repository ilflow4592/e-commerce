package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.order.OrderNotFoundException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        Long orderId = orderService.createOrder(createOrderDto);

        // then
        assertEquals(order.getId(), orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 사용자를 찾을 수 없으면 UserNotFoundException 예외를 던진다.")
    void createOrder_UserNotFound() {
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 상품을 찾을 수 없으면 ProductNotFoundException 예외를 던진다.")
    void createOrder_ProductNotFound() {
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 총 가격이 맞지 않다면 OrderTotalPriceNotCorrectException 예외를 던진다.")
    void createOrder_TotalPriceNotCorrect() {
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        CreateOrderDto incorrectDto = new CreateOrderDto(1L, 900, Map.of(1L, 2));

        // when & then
        assertThrows(OrderTotalPriceNotCorrectException.class, () -> orderService.createOrder(incorrectDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 상품의 재고가 없으면 ProductOutOfStockException 예외를 던진다.")
    void createOrder_ProductOutOfStock() {
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        product.updateStockQuantity(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then
        assertThrows(ProductOutOfStockException.class, () -> orderService.createOrder(createOrderDto));
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("존재하는 모든 주문을 조회할 수 있다.")
    void getAllOrders() {
        // given
        Page<Order> ordersPage = new PageImpl<>(Arrays.asList(order));
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findAll(pageable)).thenReturn(ordersPage);

        // when
        PageableDto<OrderDto> result = orderService.getAllOrders(pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("단일 주문을 조회할 수 있다.")
    void getOrder() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when
        OrderDto result = orderService.getOrder(1L);

        // then
        assertNotNull(result);
        assertEquals(order.getUser(), result.user());
        assertEquals(order.getTotalPrice(), result.totalPrice());
        assertEquals(order.getOrderItems(), result.orderItems());
        assertEquals(order.getOrderStatus(), result.orderStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("찾는 주문이 존재하지 않을 시, OrderNotFoundException 예외를 던진다.")
    void getOrder_OrderNotFound() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("주문을 제거할 수 있다.")
    void deleteOrder() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.deleteOrder(1L);

        // then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    @DisplayName("주문을 제거할 때 주문 항목들도 함께 제거된다.")
    void deleteOrderWithCascade() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.deleteOrder(1L);

        // then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
        assertTrue(order.getOrderItems().isEmpty());
    }

}

