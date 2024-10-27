package com.example.ecommerce.dto;

import lombok.Builder;

@Builder
public record ProductDto(
        Long id,
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity
) {


}
