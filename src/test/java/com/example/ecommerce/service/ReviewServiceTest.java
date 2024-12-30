package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.review.ReviewAlreadyExistsException;
import com.example.ecommerce.common.exception.review.ReviewException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.review.CreateReviewDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private Product product;
    private Review review;

    private CreateReviewDto createReviewDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        product = Product.builder()
                .id(2L)
                .avgRating(0)
                .build();

        review = Review.builder()
                .id(10L)
                .user(user)
                .product(product)
                .build();

        createReviewDto = CreateReviewDto.builder()
                .userId(user.getId())
                .productId(product.getId())
                .rating(4)
                .build();
    }

    @Test
    @DisplayName("유저는 리뷰를 생성할 수 있다.")
    void createReview_ShouldCreateReview_WhenValidInput() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        Long reviewId = reviewService.createReview(createReviewDto);

        // then
        assertNotNull(reviewId);
        assertEquals(10L, reviewId);
        verify(userRepository).findById(user.getId());
        verify(productRepository).findById(product.getId());
        verify(reviewRepository).findByUserIdAndProductId(user.getId(), product.getId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 생성 시, 유저 id가 없는 경우 UserNotFoundException을 던진다.")
    void createReview_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> reviewService.createReview(createReviewDto));
        assertEquals(UserException.NOTFOUND.getMessage(), exception.getMessage());
        verify(userRepository).findById(user.getId());
        verifyNoInteractions(productRepository, reviewRepository);
    }

    @Test
    @DisplayName("리뷰 생성 시, 상품 id가 없는 경우 ProductNotFoundException을 던진다.")
    void createReview_ShouldThrowProductNotFoundException_WhenProductNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // when & then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> reviewService.createReview(createReviewDto));
        assertEquals(ProductException.NOTFOUND.getMessage(), exception.getMessage());
        verify(userRepository).findById(user.getId());
        verify(productRepository).findById(product.getId());
        verifyNoInteractions(reviewRepository);
    }

    @Test
    @DisplayName("리뷰를 작성한 유저가 또 다시 리뷰를 작성한다면 ReviewAlreadyExistsExeption을 던진다.")
    void createReview_ShouldThrowReviewAlreadyExistsException_WhenReviewExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(Optional.of(review));

        // when & then
        ReviewAlreadyExistsException exception = assertThrows(ReviewAlreadyExistsException.class, () -> reviewService.createReview(createReviewDto));
        assertEquals(ReviewException.ALREADY_EXISTS.getMessage(), exception.getMessage());
        verify(userRepository).findById(user.getId());
        verify(productRepository).findById(product.getId());
        verify(reviewRepository).findByUserIdAndProductId(user.getId(), product.getId());
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("리뷰 생성 시, 제공한 rating 값에 따라 모든 리뷰를 가져와 해당 값과 합한 결과로 새로운 평균값을 계산한다.")
    void calculateProductReviewRating_ShouldUpdateProductAverageRating_WhenExistingReviewsExist() {
        // given
        float newRating = 4.0f;
        List<Review> matchedReviewsByProductId = new ArrayList<>();
        matchedReviewsByProductId.add(Review.builder().rating(3.0f).build());
        matchedReviewsByProductId.add(Review.builder().rating(5.0f).build());

        when(reviewRepository.findAllByProductId(product.getId())).thenReturn(matchedReviewsByProductId);

        // when
        reviewService.calculateProductReviewRating(newRating, product, user);

        // then
        float expectedAverageRating = 4.0f; // (3 + 5 + 4) / 3
        verify(reviewRepository).findAllByProductId(product.getId());
        assertEquals(expectedAverageRating, product.getAvgRating());
    }

}
