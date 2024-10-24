package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Product;

public record ProductDto(
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity
) {

    public static Product toEntity(ProductDto dto){
        return Product.builder()
                .name(dto.name)
                .description(dto.description)
                .unitPrice(dto.unitPrice)
                .stockQuantity(dto.stockQuantity)
                .build();
    }


}
