package com.vinaacademy.platform.feature.order_payment.service;

import java.util.List;
import java.util.UUID;

import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;

public interface OrderItemService {
	
	List<OrderItemDto> getOrderItems(UUID orderId);

}
