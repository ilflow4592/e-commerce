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
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("OrderService::verifyPaymentAndCreateOrder execution started.");

        User user = validateUser(createOrderDto.userId());
        log.debug("Called - validateUser(createOrderDto.userId()), response - user : {}",
            user);

        Order order = Order.builder()
            .user(user)
            .totalPrice(createOrderDto.totalPrice())
            .build();
        log.debug("Called - Order.builder(), response - order : {}",
            order);

        // 제품 유효성 및 가격 체크
        List<Product> products = validateProductsAndCalculateTotalPrice(createOrderDto);
        log.debug("Called - validateProductsAndCalculateTotalPrice, response - products : {}",
            products);

        // 포트원으로부터 결제 정보를 불러옴
        PortOneGetPaymentResponseDto paymentDto = portOnePayment.getPayment(paymentId);
        log.debug("Called - portOnePayment.getPayment(paymentId), response - paymentDto : {}",
            paymentDto);

        // 결제 정보 존재할 시 주문 상태 업데이트 및 저장
        order.fromCurrentOrderStatusTo(OrderStatus.PAID);
        log.debug("Called - order.fromCurrentOrderStatusTo(OrderStatus.PAID), response : NONE");

        Long orderId = orderRepository.save(order).getId();
        log.debug("Called - orderRepository.save(order).getId(), response - orderId : {}", orderId);

        // 주문 아이템 생성 및 벌크 저장
        saveOrderItems(order, products, createOrderDto.productsMap());
        log.debug(
            "Called - saveOrderItems(order, products, createOrderDto.productsMap()), response : NONE");

        Payment payment = PortOneGetPaymentResponseDto.toEntity(paymentDto, orderId);
        log.debug(
            "Called - PortOneGetPaymentResponseDto.toEntity(paymentDto, orderId), response - payment : {}",
            payment);

        paymentRepository.save(payment);
        log.debug("Called - paymentRepository.save(payment), response : NONE");

        log.info("OrderService::verifyPaymentAndCreateOrder execution successfully ended.");
        return order.getId();
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

    private void saveOrderItems(Order order, List<Product> products,
        Map<Long, Integer> productsMap) {
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

            product.updateStockQuantity(product.getStockQuantity() - quantity);

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
    public PageableDto<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> pageableOrders = orderRepository.findAll(pageable);

        return PageableDto.toDto(pageableOrders.map(Order::toDto));
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(OrderException.NOTFOUND.getStatus(),
                OrderException.NOTFOUND.getMessage()));

        return Order.toDto(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(OrderException.NOTFOUND.getStatus(),
                OrderException.NOTFOUND.getMessage()));

        orderRepository.delete(order);
    }

}
