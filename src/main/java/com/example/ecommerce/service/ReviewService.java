package com.example.ecommerce.service;

import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.dto.review.UpdateReviewDto;

public interface ReviewService {

    Long createReview(CreateReviewDto createReviewDto);
    ReviewDto getReview(Long productId, Long userId);
    ReviewDto updateReview(Long productId, Long userId, UpdateReviewDto updateReviewDto);
}