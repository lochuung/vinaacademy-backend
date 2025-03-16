package com.vinaacademy.platform.feature.order_payment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.cart.entity.Cart;
import com.vinaacademy.platform.feature.cart.entity.CartItem;
import com.vinaacademy.platform.feature.cart.repository.CartItemRepository;
import com.vinaacademy.platform.feature.cart.repository.CartRepository;
import com.vinaacademy.platform.feature.order_payment.dto.OrderDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderRequest;
import com.vinaacademy.platform.feature.order_payment.entity.Coupon;
import com.vinaacademy.platform.feature.order_payment.entity.Order;
import com.vinaacademy.platform.feature.order_payment.entity.OrderItem;
import com.vinaacademy.platform.feature.order_payment.enums.OrderStatus;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderItemMapper;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderMapper;
import com.vinaacademy.platform.feature.order_payment.repository.CouponRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderItemRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService{
	
	private UserRepository userRepository;
	
	private OrderItemRepository orderItemRepository;
	
	private OrderRepository orderRepository;
	
	private CartRepository cartRepository;
	
	private CartItemRepository cartItemRepository;
	
	private OrderItemMapper orderItemMapper;
	
	private OrderMapper orderMapper;
	
	private CouponRepository couponRepository;

	
	@Override
	public List<OrderItemDto> getOrderItems(UUID orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		List<OrderItemDto> orderItemDtos = orderItemMapper.toOrderItemDtoList(order.getOrderItems());
		return orderItemDtos;
	}


}
