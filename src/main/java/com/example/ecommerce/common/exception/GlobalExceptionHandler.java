package com.example.ecommerce.common.exception;


import com.example.ecommerce.common.exception.order.OrderTotalPriceNotCorrectException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductOutOfStockException;
import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
