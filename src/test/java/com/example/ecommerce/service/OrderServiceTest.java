package com.example.ecommerce.service;

import com.example.ecommerce.api.port_one.PortOnePayment;
import com.example.ecommerce.common.exception.order.OrderNotFoundException;
import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
import com.example.ecommerce.dto.port_one.PortOneGetPaymentResponseDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Mock
    private PortOnePayment portOnePayment;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderDto createOrderDto;
    private User user;
    private Product product;
    private Order order;
    private PortOneGetPaymentResponseDto portOneGetPaymentResponseDto;
    private Payment payment;
    private String paymentId = "payment-7b416578-cde2-4871-9884-bde4af01c508";

    @BeforeEach
    void setUp() {
        //userId가 1L이라고 가정한 주문한 상품 2개의 총 가격은 = 100,000 (개당 50,000)
        createOrderDto = new CreateOrderDto(1L, 100000, Map.of(1L, 2));

        user = new User("ILYA", "test123@gmail.com","1234","01012341234");
        user.setId(1L);

        product = new Product("패딩 점퍼", "방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.", 50000, 10);
        product.setId(1L);

//        //50,000*2=100,000
        order = Order.builder()
                .user(user)
                .totalPrice(100000)
                .orderItems(new ArrayList<>())
                .build();
        order.setId(1L);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime paidAt = LocalDateTime.parse("2024-11-01 12:59:16.773693", formatter);

        //포트원 응답값
        portOneGetPaymentResponseDto = PortOneGetPaymentResponseDto.builder()
                .id(paymentId)
                .status("PAID")
                .transactionId("0192e18e-152c-33d8-3a0a-6a88e0eb17b6")
                .merchantId("merchant-a633191f-848d-41f3-9621-128f52d3b187")
                .method(new PortOneGetPaymentResponseDto.PaymentMethod("PaymentMethodEasyPay", "KAKAOPAY"))
                .paidAt(paidAt)
                .build();

        //데이터베이스에 넣을 값
        payment = Payment.builder()
                .paymentId("payment-9d9af42a-f9f9-4920-b1c0-6f173e80221b")
                .transactionId("0192e7ce-c35a-76d9-5ab3-87c7b4d5dcf6")
                .merchantId("merchant-a633191f-848d-41f3-9621-128f52d3b187")
                .orderId(1L)
                .paymentMethodType("PaymentMethodEasyPay")
                .provider("KAKAOPAY")
                .paidAt(paidAt)
                .build();

    }

    @Test
    @Transactional
    @DisplayName("사용자는 주문을 할 수 있다.")
    void verifyPaymentAndCreateOrder() {
        //given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(portOnePayment.getPayment(paymentId)).thenReturn(portOneGetPaymentResponseDto);

        // save 호출 시 ID가 설정된 order 반환
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);  // 테스트에서 사용할 ID 설정
            return savedOrder;
        });

        // when
        Long orderId = orderService.verifyPaymentAndCreateOrder(paymentId, createOrderDto);

        // then
        assertNotNull(orderId);
        assertEquals(order.getId(), orderId);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("주문 시 사용자를 찾을 수 없으면 UserNotFoundException 예외를 던진다.")
    void verifyPaymentAndCreateOrder_UserNotFound() {
        // given
        Long invalidUserId = 999L;
        CreateOrderDto createOrderDto = new CreateOrderDto(invalidUserId, 10000, Map.of(1L, 1));

        when(userRepository.findById(invalidUserId)).thenThrow(new UserNotFoundException(UserException.NOTFOUND.getStatus(), UserException.NOTFOUND.getMessage()));

        // when / then
        assertThrows(UserNotFoundException.class,
                () -> orderService.verifyPaymentAndCreateOrder(paymentId, createOrderDto));
    }

    @Test
    @DisplayName("주문 시 상품을 찾을 수 없으면 ProductNotFoundException 예외를 던진다.")
    void verifyPaymentAndCreateOrder_ProductNotFound() {
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> orderService.verifyPaymentAndCreateOrder(paymentId, createOrderDto));
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 시 총 가격이 맞지 않다면 OrderTotalPriceNotCorrectException 예외를 던진다.")
    void verifyPaymentAndCreateOrder_OrderTotalPriceNotCorrect() {
        // given
        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        CreateOrderDto incorrectDto = new CreateOrderDto(1L, 900, Map.of(1L, 2));

        // when / then
        assertThrows(OrderTotalPriceNotCorrectException.class,
                () -> orderService.verifyPaymentAndCreateOrder(paymentId, incorrectDto));
    }

    @Test
    @DisplayName("주문 시 상품의 재고가 없으면 ProductOutOfStockException 예외를 던진다.")
    void verifyPaymentAndCreateOrder_ProductOutOfStock() {
        // given
        product.updateStockQuantity(0);  // 재고가 부족한 상태로 설정

        when(userRepository.findById(createOrderDto.userId())).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when / then
        assertThrows(ProductOutOfStockException.class,
                () -> orderService.verifyPaymentAndCreateOrder(paymentId, createOrderDto));
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

