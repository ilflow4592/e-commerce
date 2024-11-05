package com.example.ecommerce.service;

import com.example.ecommerce.api.port_one.PortOnePayment;
import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.common.exception.order.OrderException;
import com.example.ecommerce.common.exception.order.OrderNotFoundException;
import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.dto.order.OrderDto;
import com.example.ecommerce.dto.port_one.PortOneGetPaymentResponseDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;


    private final PortOnePayment portOnePayment;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Long verifyPaymentAndCreateOrder(String paymentId, CreateOrderDto createOrderDto) {

        // 사용자 유효성 체크
        User user = validateUser(createOrderDto.userId());

        Order order = Order.builder()
                .user(user)
                .totalPrice(createOrderDto.totalPrice())
                .build();

        // 제품 유효성 및 가격 체크
        List<Product> products = validateProductsAndCalculateTotalPrice(createOrderDto);

        // 포트원으로부터 결제 정보를 불러옴
        PortOneGetPaymentResponseDto paymentDto = portOnePayment.getPayment(paymentId);

        // 결제 정보 존재할 시 주문 상태 업데이트 및 저장
        order.fromCurrentOrderStatusTo(OrderStatus.PAID);
        Long orderId = orderRepository.save(order).getId();

        // 주문 아이템 생성 및 벌크 저장
        saveOrderItems(order, products, createOrderDto.productsMap());

        Payment payment = PortOneGetPaymentResponseDto.toEntity(paymentDto, orderId);
        paymentRepository.save(payment);

        return order.getId();
    }

    @Override
    public PageableDto<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> pageableOrders = orderRepository.findAll(pageable);

        return PageableDto.toDto(pageableOrders.map(Order::toDto));
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        UserException.NOTFOUND.getStatus(),
                        UserException.NOTFOUND.getMessage())
                );
    }

    private List<Product> validateProductsAndCalculateTotalPrice(CreateOrderDto createOrderDto) {
        int totalPrice = createOrderDto.totalPrice();
        List<Product> products = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : createOrderDto.productsMap().entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(
                            ProductException.NOTFOUND.getStatus(),
                            ProductException.NOTFOUND.getMessage())
                    );

            products.add(product);

            totalPrice -= product.getUnitPrice() * quantity;
        }

        if (totalPrice != 0) {
            throw new OrderTotalPriceNotCorrectException(
                    OrderException.NOT_CORRECT.getStatus(),
                    OrderException.NOT_CORRECT.getMessage()
            );
        }

        return products;
    }

    private void saveOrderItems(Order order, List<Product> products, Map<Long, Integer> productsMap) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (Product product : products) {
            Integer quantity = productsMap.get(product.getId());

            // 재고 체크
            if ((product.getStockQuantity() - quantity) <= 0) {
                throw new ProductOutOfStockException(
                        ProductException.OUT_OF_STOCK.getStatus(),
                        ProductException.OUT_OF_STOCK.getMessage()
                );
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getUnitPrice() * quantity)
                    .build();

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);  // 벌크 저장으로 성능 최적화
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(OrderException.NOTFOUND.getStatus(), OrderException.NOTFOUND.getMessage()));

        return Order.toDto(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(OrderException.NOTFOUND.getStatus(), OrderException.NOTFOUND.getMessage()));

        orderRepository.delete(order);
    }

}
