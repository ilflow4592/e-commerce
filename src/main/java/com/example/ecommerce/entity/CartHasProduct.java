package com.example.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartHasProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartHasProductId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder.Default
    @NotNull
    private Integer quantity = 1;

    @NotNull
    private Integer totalPrice;

    public void updateQuantityWithTotalPrice(Integer quantity, Integer unitPrice){
        this.quantity = quantity;
        this.totalPrice = quantity * unitPrice;
    }

}
