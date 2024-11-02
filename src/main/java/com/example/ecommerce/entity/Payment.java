package com.example.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity(name="payments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    private String paymentId;

    @NotNull
    private String transactionId;

    @NotNull
    private String merchantId;

    @NotNull
    private Long orderId;

    @NotNull
    private String paymentMethodType;

    @NotNull
    private String provider;

    @NotNull
    private LocalDateTime paidAt;


}
