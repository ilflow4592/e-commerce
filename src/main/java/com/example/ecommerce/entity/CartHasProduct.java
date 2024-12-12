package com.example.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartHasProduct extends BaseEntity {

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

    public void addProductUpdate(Integer quantity, Integer unitPrice){
        this.quantity += quantity;
        this.totalPrice += quantity*unitPrice;
    }

    public void removeProductUpdate(Integer unitPrice){
        this.quantity -= 1;
        this.totalPrice -= unitPrice;
    }
}
