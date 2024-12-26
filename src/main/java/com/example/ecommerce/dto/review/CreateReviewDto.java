package com.example.ecommerce.dto.review;

import com.example.ecommerce.common.validator.ValidFraction;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;


public record CreateReviewDto(
    Long userId,
    Long productId,
    @DecimalMin(value = "0", message = "리뷰는 최소 0점 이상이어야 합니다.")
    @DecimalMax(value = "5", message = "리뷰는 최대 5점 이하여야 합니다.")
    @ValidFraction
    float rating,
    @Nullable
    @Size(max = 1000, message = "1000자를 넘어갈 수 없습니다.")
    String comment
) {
    public static Review toEntity(CreateReviewDto dto, User user, Product product) {
        return Review.builder()
                .user(user)
                .product(product)
                .rating(dto.rating)
                .comment(dto.comment)
                .build();
    }

}