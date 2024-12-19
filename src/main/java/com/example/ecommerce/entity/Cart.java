package com.example.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartHasProduct> cartHasProducts;

    public void addProduct(Product product, Integer quantity) {
        this.cartHasProducts.add(
                CartHasProduct.builder()
                .cart(this)
                .product(product)
                .quantity(quantity)
                .totalPrice(product.getUnitPrice() * quantity)
                .build()
        );
    }

    public void removeProduct(Long productId){
        this.cartHasProducts.removeIf(cartHasProduct ->
                cartHasProduct.getProduct().getId().equals(productId)
        );
    }

}
