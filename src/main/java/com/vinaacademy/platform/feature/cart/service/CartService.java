package com.vinaacademy.platform.feature.cart.service;

import java.util.UUID;

import com.vinaacademy.platform.feature.cart.dto.CartDto;
import com.vinaacademy.platform.feature.cart.dto.CartRequest;

public interface CartService {
	
	 CartDto getCart(UUID userId);
	 
	 CartDto createCart(CartRequest request);

	 CartDto updateCart(CartRequest request);
	 
	 void deleteCart(CartRequest request);
	
}
