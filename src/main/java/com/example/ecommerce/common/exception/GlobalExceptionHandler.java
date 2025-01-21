package com.example.ecommerce.common.exception;


import com.example.ecommerce.common.exception.order.OrderNotFoundException;
import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import com.example.ecommerce.common.exception.order_item.OrderItemNotFoundException;
import com.example.ecommerce.common.exception.port_one.PortOneNotFoundPaymentException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.review.ReviewAlreadyExistsException;
import com.example.ecommerce.common.exception.review.ReviewNotFoundException;
import com.example.ecommerce.common.exception.user.PasswordDontMatchException;
import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 공통 에러 응답 생성 메서드
    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }

    /**
     * Validation Error Exception - Bean Validation (@Valid) 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        record FieldErrorMessage(String error, String message) {}

        List<FieldErrorMessage> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorMessage(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * User Exception
     */
    @ExceptionHandler(UserEmailDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleUserEmailDuplicateException(UserEmailDuplicateException ex) {
        log.warn("UserEmailDuplicateException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("UserNotFoundException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(PasswordDontMatchException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordDontMatchException(PasswordDontMatchException ex) {
        log.warn("PasswordDontMatchException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     *Product Exception
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException ex) {
        log.warn("ProductNotFoundException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<Map<String, Object>> handleProductOutOfStockException(ProductOutOfStockException ex) {
        log.warn("ProductOutOfStockException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * Order Exception
     */
    @ExceptionHandler(OrderTotalPriceNotCorrectException.class)
    public ResponseEntity<Map<String, Object>> handleOrderTotalPriceNotCorrectException(OrderTotalPriceNotCorrectException ex) {
        log.warn("OrderTotalPriceNotCorrectException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.warn("OrderNotFoundException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * OrderItem Exception
     */
    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderItemNotFoundException(OrderItemNotFoundException ex) {
        log.warn("OrderItemNotFoundException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * PortOne Exception
     */
    @ExceptionHandler(PortOneNotFoundPaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePortOneNotFoundPaymentException(PortOneNotFoundPaymentException ex) {
        log.warn("PortOneNotFoundPaymentException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * Review Exception
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReviewNotFoundException(ReviewNotFoundException ex) {
        log.warn("ReviewNotFoundException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleReviewAlreadyExistsException(ReviewAlreadyExistsException ex) {
        log.warn("ReviewAlreadyExistsException 발생: {}", ex.getMessage(), ex);

        return errorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
