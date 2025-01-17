package com.example.ecommerce.dto.product;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductDto(
        Long id,
        @NotBlank(message = "상품 이름이 빈 문자열일 수 없습니다.")
        String name,

        @NotBlank(message = "상품 설명이 빈 문자열일 수 없습니다.")
        String description,

        @NotNull
        @Min(value = 10000, message = "개당 가격은 10,000원 이상입니다.")
        Integer unitPrice,

        @NotNull
        @Min(value = 1, message = "재고는 0일 수 없습니다.")
        Integer stockQuantity,

        Category category,
        Size size,
        Boolean shopDisplayable,
        @Nullable
        String fileUrl
) {
}
