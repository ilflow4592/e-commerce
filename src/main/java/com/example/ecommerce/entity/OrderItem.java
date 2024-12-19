package com.example.ecommerce.entity;

import com.example.ecommerce.dto.order_item.OrderItemDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "order_items")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
               .orderItemId(orderItem.getId())
               .price(orderItem.product.getUnitPrice())
               .quantity(orderItem.product.getStockQuantity())
               .orderId(orderItem.order.getId())
               .productId(orderItem.product.getId())
               .build();
    }
}
