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
public class OrderServiceImpl implements OrderService{
	
	private UserRepository userRepository;
	
	private OrderItemRepository orderItemRepository;
	
	private OrderRepository orderRepository;
	
	private CartRepository cartRepository;
	
	private CartItemRepository cartItemRepository;
	
	private OrderItemMapper orderItemMapper;
	
	private OrderMapper orderMapper;
	
	private CouponRepository couponRepository;

	@Override
	public OrderDto createOrder(UUID userId) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy user này"));
		
		Cart cart = cartRepository.findByUserId(userId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy Cart của user id này"));
		
		List<CartItem> cartItems = cart.getCartItems();
		Order order = Order.builder()
				.status(OrderStatus.PENDING)
				.coupon(cart.getCoupon())
				.payment(null)
				.user(user)
				.build();
		orderRepository.save(order);
		List<OrderItem> orderItems = orderItemMapper.toOrderItemList(cartItems, order);
		orderItemRepository.saveAll(orderItems);
		
//		//xoa all item trong cart
//		cartItemRepository.deleteByCart(cart);
		
		OrderDto orderDto = orderMapper.toOrderDto(order);
		return orderDto;
	}

	@Override
	public void deleteOrder(UUID orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		orderRepository.delete(order);
		
	}

	@Override
	public OrderDto updateOrder(OrderRequest orderRequest) {
		Order order = orderRepository.findById(orderRequest.getOrderId()).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		Coupon coupon = null;
		if (orderRequest.getCouponId() != null) {
			coupon = couponRepository.findById(orderRequest.getCouponId())
					.orElseThrow(() -> BadRequestException.message("Không tìm thấy coupon"));
		}
		order.setCoupon(coupon);
		OrderDto orderDto = orderMapper.toOrderDto(orderRepository.save(order));
		return orderDto;
	}

	@Override
	public OrderDto getOrder(UUID orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		OrderDto orderDto = orderMapper.toOrderDto(order);
		return orderDto;
	}



}
