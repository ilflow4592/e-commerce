package com.example.ecommerce.entity;

import com.example.ecommerce.common.enums.order.OrderStatus;
import com.example.ecommerce.dto.order.OrderDto;
import com.example.ecommerce.dto.user.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;
import java.util.List;

@Getter
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
    @Cascade(CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder.Default
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public void fromCurrentOrderStatusTo(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

    public static OrderDto toDto(Order order){
        return OrderDto.builder()
                .userDto(UserDto.toDto(order.getUser()))
                .totalPrice(order.getTotalPrice())
                .orderItems(order.getOrderItems())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
