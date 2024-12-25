package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.review.ReviewException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.common.exception.review.ReviewNotFoundException;
import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService{

    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Long createReview(CreateReviewDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(
                        UserException.NOTFOUND.getStatus(),
                        UserException.NOTFOUND.getMessage()
                ));

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(
                        ProductException.NOTFOUND.getStatus(),
                        ProductException.NOTFOUND.getMessage()
                ));

        Review review = CreateReviewDto.toEntity(dto, user, product);

        return reviewRepository.save(review).getId();
    }

    @Override
    public ReviewDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        ReviewException.NOTFOUND.getStatus(),
                        ReviewException.NOTFOUND.getMessage()
                ));

        return Review.toDto(review);
    }
}
