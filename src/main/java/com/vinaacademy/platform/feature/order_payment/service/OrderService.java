package com.vinaacademy.platform.feature.order_payment.service;

import com.vinaacademy.platform.feature.order_payment.dto.OrderDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderRequest;

import java.util.UUID;

public interface OrderService {
	
	OrderDto createOrder(UUID userId);
	
	void deleteOrder(UUID orderId);
	
	OrderDto updateOrder(OrderRequest orderRequest);
	
	OrderDto getOrder(UUID orderId);

}
