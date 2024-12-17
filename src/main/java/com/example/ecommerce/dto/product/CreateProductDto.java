package com.example.ecommerce.dto.product;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.entity.Product;
import lombok.Builder;

@Builder
public record CreateProductDto(
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity,
        Category category,
        Size size
) {

    public static Product toEntity(CreateProductDto dto){
        return Product.builder()
                .name(dto.name)
                .description(dto.description)
                .unitPrice(dto.unitPrice)
                .stockQuantity(dto.stockQuantity)
                .category(dto.category)
                .size(dto.size)
                .build();
    }

}
