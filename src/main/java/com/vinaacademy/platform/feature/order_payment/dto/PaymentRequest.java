package com.vinaacademy.platform.feature.order_payment.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.vinaacademy.platform.feature.order_payment.enums.PaymentStatus;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private UUID paymentId;
    private JsonNode paymentData;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String transactionId;
}