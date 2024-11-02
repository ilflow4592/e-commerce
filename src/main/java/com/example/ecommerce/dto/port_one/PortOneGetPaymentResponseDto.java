package com.example.ecommerce.dto.port_one;

import com.example.ecommerce.entity.Payment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PortOneGetPaymentResponseDto(
        String id, //paymentId
        String status,
        String transactionId,
        String merchantId,
        PaymentMethod method,
        LocalDateTime paidAt,
        List<PortOneProduct> products
) {

    public record PaymentMethod(String type, String provider) {
    }

    public static Payment toEntity(PortOneGetPaymentResponseDto dto, Long orderId){
        return Payment.builder()
                .paymentId(dto.id())
                .transactionId(dto.transactionId())
                .merchantId(dto.merchantId())
                .orderId(orderId)
                .paymentMethodType(dto.method().type())
                .provider(dto.method().provider())
                .paidAt(dto.paidAt())
                .build();
    }
}


