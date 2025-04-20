package com.vinaacademy.platform.feature.order_payment.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.cart.repository.CartItemRepository;
import com.vinaacademy.platform.feature.cart.repository.CartRepository;
import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;
import com.vinaacademy.platform.feature.order_payment.entity.Order;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderItemMapper;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderMapper;
import com.vinaacademy.platform.feature.order_payment.repository.CouponRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderItemRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService{
	
	private final UserRepository userRepository;
	
	private final OrderItemRepository orderItemRepository;
	
	private final OrderRepository orderRepository;
	
	private final CartRepository cartRepository;
	
	private final CartItemRepository cartItemRepository;
	
	private final OrderItemMapper orderItemMapper;
	
	private final OrderMapper orderMapper;
	
	private final CouponRepository couponRepository;

	
	@Override
	public List<OrderItemDto> getOrderItems(UUID orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		List<OrderItemDto> orderItemDtos = orderItemMapper.toOrderItemDtoList(order.getOrderItems());
		return orderItemDtos;
	}


}
