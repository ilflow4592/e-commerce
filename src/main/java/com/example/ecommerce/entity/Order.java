package com.example.ecommerce.entity;

import com.example.ecommerce.common.enums.order.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@AttributeOverride(name = "id", column = @Column(name = "orderId"))
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @NotNull
    private Integer totalPrice;

    @Builder.Default
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder.Default
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public void fromCurrentOrderStatusTo(OrderStatus orderStatus){
        this.orderStatus = orderStatus;

    }
}
