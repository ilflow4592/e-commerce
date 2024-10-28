package com.example.ecommerce.service;


import com.example.ecommerce.common.exception.order_item.OrderItemException;
import com.example.ecommerce.common.exception.order_item.OrderItemNotFoundException;
import com.example.ecommerce.dto.order_item.OrderItemDto;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.repository.OrderItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemServiceImpl implements OrderItemService{

    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItemDto getOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(OrderItemException.NOTFOUND.getStatus(), OrderItemException.NOTFOUND.getMessage()));

        return OrderItem.toDto(orderItem);
    }
}
