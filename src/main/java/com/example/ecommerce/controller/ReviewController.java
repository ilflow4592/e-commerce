package com.example.ecommerce.controller;

import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Long> createReview(@Valid @RequestBody CreateReviewDto createReviewDto){
        Long reviewId = reviewService.createReview(createReviewDto);
        return new ResponseEntity<>(reviewId, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id){
        ReviewDto reviewDto = reviewService.getReview(id);
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }
}