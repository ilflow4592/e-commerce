package com.example.ecommerce.dto.review;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ReviewDto(
        Long id,
        Long userId,
        Long productId,
        Float rating,
        @Nullable
        @Size(max = 1000)
        String comment
) {
}