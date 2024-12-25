package com.example.ecommerce.service;

import com.example.ecommerce.dto.review.ReviewDto;

public interface ReviewService {

    Long createReview(ReviewDto reviewDto);
}