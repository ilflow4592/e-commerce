package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.review.ReviewAlreadyExistsException;
import com.example.ecommerce.common.exception.review.ReviewException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.common.exception.review.ReviewNotFoundException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.dto.review.ReviewDto;
import com.example.ecommerce.dto.review.UpdateReviewDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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


        // 사용자가 이미 리뷰를 작성했는지 확인 - 특정 상품에 대한 리뷰는 사용자당 한번만 작성 가능
        reviewRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .ifPresent(review -> {
                    throw new ReviewAlreadyExistsException(
                            ReviewException.ALREADY_EXISTS.getStatus(),
                            ReviewException.ALREADY_EXISTS.getMessage()
                    );
                });

        calculateProductReviewRating(dto.rating(), product, user);

        Review review = CreateReviewDto.toEntity(dto, user, product);

        return reviewRepository.save(review).getId();
    }

    protected void calculateProductReviewRating(float rating, Product product, User user) {
        List<Review> matchedReviewsByProductId = reviewRepository.findAllByProductId(product.getId());

        if (!matchedReviewsByProductId.isEmpty()) {
            double totalRating = matchedReviewsByProductId.stream()
                    .mapToDouble(Review::getRating)
                    .sum() + rating;

            float newAverageRating = (float) (totalRating / (matchedReviewsByProductId.size() + 1)); // 평균 계산

            // 소숫점 둘째 자리에서 반올림 처리 후 0~5 사이로 값 제한
            newAverageRating = Math.max(0.0f, Math.min(Math.round(newAverageRating * 100) / 100.0f, 5.0f));

            product.updateAvgRating(newAverageRating);
        } else {
            log.info("id가 " + user.getId() + " 인 유저가 " + product.getName() + " 상품의 첫 리뷰를 작성하셨습니다.");

            product.updateAvgRating(rating);
        }
    }

    @Override
    public ReviewDto getReview(Long productId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        UserException.NOTFOUND.getStatus(),
                        UserException.NOTFOUND.getMessage()
                ));

        productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        ProductException.NOTFOUND.getStatus(),
                        ProductException.NOTFOUND.getMessage()
                ));

        return reviewRepository.findByUserIdAndProductId(userId, productId)
                .map(ReviewDto::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(
                        ReviewException.NOTFOUND.getStatus(),
                        ReviewException.NOTFOUND.getMessage()
                ));

    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long productId, Long userId, UpdateReviewDto updateReviewDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        UserException.NOTFOUND.getStatus(),
                        UserException.NOTFOUND.getMessage()
                ));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        ProductException.NOTFOUND.getStatus(),
                        ProductException.NOTFOUND.getMessage()
                ));

        Review review = reviewRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        ReviewException.NOTFOUND.getStatus(),
                        ReviewException.NOTFOUND.getMessage()
                ));

        if(updateReviewDto.rating() != review.getRating()){
            calculateProductReviewRating(updateReviewDto.rating(), product, user);
        }

        review.update(updateReviewDto.rating(), updateReviewDto.comment());

        return Review.toDto(review);
    }

    @Override
    public PageableDto<ReviewDto> getReviewsByProductId(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        ProductException.NOTFOUND.getStatus(),
                        ProductException.NOTFOUND.getMessage()
                ));

        Page<Review> pageableReviews = reviewRepository.findAllByProductId(product.getId(), pageable);

        return PageableDto.toDto(pageableReviews.map(Review::toDto));
    }

    @Override
    public PageableDto<ReviewDto> getAllReviews(Pageable pageable) {
        Page<Review> pageableReviews = reviewRepository.findAll(pageable);

        return PageableDto.toDto(pageableReviews.map(Review::toDto));
    }
}
