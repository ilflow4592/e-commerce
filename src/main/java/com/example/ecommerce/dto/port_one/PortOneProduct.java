package com.example.ecommerce.dto.port_one;

public record PortOneProduct(
        String id,
        String name,
        Integer amount,
        Integer quantity
) {
}
