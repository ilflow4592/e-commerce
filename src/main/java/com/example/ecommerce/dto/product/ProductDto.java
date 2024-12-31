package com.example.ecommerce.dto.product;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import lombok.Builder;

@Builder
public record ProductDto(
        Long id,
        String name,
        String description,
        Integer unitPrice,
        Integer stockQuantity,
        Category category,
        Size size
) {
}
