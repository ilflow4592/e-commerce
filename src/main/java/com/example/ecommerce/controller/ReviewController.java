package com.example.ecommerce.controller;

import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Long> createReview(@Valid @RequestBody ReviewDto reviewDto){
        Long reviewId = reviewService.createReview(reviewDto);
        return new ResponseEntity<>(reviewId, HttpStatus.CREATED);
    }
}