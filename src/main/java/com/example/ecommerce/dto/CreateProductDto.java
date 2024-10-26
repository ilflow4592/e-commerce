package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Product;
import lombok.Builder;

@Builder
public record CreateProductDto(
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity
) {

    public static Product toEntity(CreateProductDto dto){
        return Product.builder()
                .name(dto.name)
                .description(dto.description)
                .unitPrice(dto.unitPrice)
                .stockQuantity(dto.stockQuantity)
                .build();
    }
    
}
