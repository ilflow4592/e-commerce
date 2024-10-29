package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.common.exception.order.OrderException;
import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.order.CreateOrderDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public Long createOrder(CreateOrderDto createOrderDto) {
        // 사용자 유효성 체크
        User user = validateUser(createOrderDto.userId());

        Order order = Order.builder()
                .user(user)
                .totalPrice(createOrderDto.totalPrice())
                .build();

        // 제품 유효성 및 가격 체크
        List<Product> products = validateProductsAndCalculateTotalPrice(createOrderDto);

        /**
         * TODO: 결제 로직 추가(성공 - OrderStatus.PAID, 실패 - OrderStatus.FAILED)
         * 그전까지는 그냥 결제를 했다고 가정
         * try, catch로 묶어서 처리
         */

        // 주문 성공 시 상태 업데이트
        order.fromCurrentOrderStatusTo(OrderStatus.PAID);
        orderRepository.save(order);

        // 주문 아이템 생성 및 벌크 저장
        saveOrderItems(order, products, createOrderDto.productsMap());

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

    private void saveOrderItems(Order order, List<Product> products, Map<Long, Integer> productsMap) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (Product product : products) {
            Integer quantity = productsMap.get(product.getId());

            // 재고 체크
            if ((product.getStockQuantity() - quantity) < 0) {
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
}
