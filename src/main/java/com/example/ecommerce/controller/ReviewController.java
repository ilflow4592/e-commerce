package com.example.ecommerce.controller;

import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.dto.review.UpdateReviewDto;
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

    //특정 상품의 특정 사용자가 쓴 리뷰 - 가져오기
    @GetMapping("{productId}/{userId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable Long productId,
            @PathVariable Long userId
            ){
        ReviewDto reviewDto = reviewService.getReview(productId, userId);
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }

    //특정 상품의 특정 사용자가 쓴 리뷰 - 수정하기
    @PatchMapping("{productId}/{userId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long productId,
            @PathVariable Long userId,
            @RequestBody UpdateReviewDto updateReviewDto
    ){
        ReviewDto reviewDto = reviewService.updateReview(productId, userId, updateReviewDto);
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);

    }

    //특정 상품에 대한 모든 리뷰

    //모든 리뷰 - 페이지네이션 처리
}