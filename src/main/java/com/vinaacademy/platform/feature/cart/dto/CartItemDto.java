package com.vinaacademy.platform.feature.cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private UUID courseId;
    private BigDecimal price;
    private LocalDateTime addedAt;
}
