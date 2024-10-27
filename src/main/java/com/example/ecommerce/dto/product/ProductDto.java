package com.example.ecommerce.dto.product;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ProductDto(
        Long id,
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity
) {


}
