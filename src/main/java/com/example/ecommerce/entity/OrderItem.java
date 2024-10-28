package com.example.ecommerce.entity;

import com.example.ecommerce.dto.order_item.OrderItemDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name = "order_items")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity{

    @NotNull
    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @OneToOne
    @JoinColumn(name = "productId")
    private Product product;

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer price;

    public static OrderItemDto toDto(OrderItem orderItem){
       return OrderItemDto.builder()
               .id(orderItem.getId())
               .price(orderItem.product.getUnitPrice())
               .quantity(orderItem.product.getStockQuantity())
               .orderId(orderItem.order.getId())
               .productId(orderItem.product.getId())
               .build();
    }
}
