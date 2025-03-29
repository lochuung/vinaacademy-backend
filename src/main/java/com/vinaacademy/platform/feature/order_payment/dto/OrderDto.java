package com.vinaacademy.platform.feature.order_payment.dto;

import com.vinaacademy.platform.feature.order_payment.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private UUID id;
    private OrderStatus status;
    private BigDecimal subTotal;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private UUID coupon_id;
    private UUID user_id;
    private UUID payment_id;
}