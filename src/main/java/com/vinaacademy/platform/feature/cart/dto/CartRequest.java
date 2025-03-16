package com.vinaacademy.platform.feature.cart.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

	private Long id;

	private UUID coupon_id;

	private UUID user_id;

}