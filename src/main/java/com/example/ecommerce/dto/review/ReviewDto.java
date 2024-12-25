package com.example.ecommerce.dto.review;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;


public record ReviewDto(
    Long userId,
    Long productId,
    Float rating,
    @Nullable
    @Size(max = 1000)
    String comment
) {
    public static Review toEntity(ReviewDto dto, User user, Product product) {
        return Review.builder()
                .user(user)
                .product(product)
                .rating(dto.rating)
                .comment(dto.comment)
                .build();
    }

}