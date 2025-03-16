package com.vinaacademy.platform.feature.order_payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
	private Long id;
    private UUID order_id;
    private UUID course_id;
    private BigDecimal price;
}
