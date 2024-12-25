package com.example.ecommerce.service;

import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;

public interface ReviewService {

    Long createReview(CreateReviewDto createReviewDto);
    ReviewDto getReview(Long reviewId);
}