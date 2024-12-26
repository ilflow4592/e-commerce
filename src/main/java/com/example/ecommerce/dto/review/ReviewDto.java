package com.example.ecommerce.dto.review;

import com.example.ecommerce.entity.Review;
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
    public static ReviewDto toDto(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .userId(review.getUser().getId())
            .productId(review.getProduct().getId())
            .rating(review.getRating())
            .comment(review.getComment())
            .build();
    }
}