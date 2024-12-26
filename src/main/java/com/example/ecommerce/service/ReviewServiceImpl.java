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
import lombok.extern.slf4j.Slf4j;
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

        List<Review> matchedReviewsByProductId = reviewRepository.findAllByProductId(product.getId());

        if (!matchedReviewsByProductId.isEmpty()) {
            double totalRating = matchedReviewsByProductId.stream()
                    .mapToDouble(Review::getRating)
                    .sum() + dto.rating();

            float newAverageRating = (float) (totalRating / (matchedReviewsByProductId.size() + 1)); // 평균 계산

            // 소숫점 둘째 자리에서 반올림 처리 후 0~5 사이로 값 제한
            newAverageRating = Math.max(0.0f, Math.min(Math.round(newAverageRating * 100) / 100.0f, 5.0f));

            product.updateAvgRating(newAverageRating);
        } else {
            log.info("id가 " + user.getId() + " 인 유저가 " + product.getName() + " 상품의 첫 리뷰를 작성하셨습니다.");

            product.updateAvgRating(dto.rating());
        }

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
