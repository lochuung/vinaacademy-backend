package com.vinaacademy.platform.feature.order_payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.cart.repository.CartItemRepository;
import com.vinaacademy.platform.feature.cart.repository.CartRepository;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentDto;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentRequest;
import com.vinaacademy.platform.feature.order_payment.entity.Order;
import com.vinaacademy.platform.feature.order_payment.entity.Payment;
import com.vinaacademy.platform.feature.order_payment.enums.PaymentStatus;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderItemMapper;
import com.vinaacademy.platform.feature.order_payment.mapper.OrderMapper;
import com.vinaacademy.platform.feature.order_payment.mapper.PaymentMapper;
import com.vinaacademy.platform.feature.order_payment.repository.CouponRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderItemRepository;
import com.vinaacademy.platform.feature.order_payment.repository.OrderRepository;
import com.vinaacademy.platform.feature.order_payment.repository.PaymentRepository;
import com.vinaacademy.platform.feature.order_payment.utils.VNPayConfig;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;

import ch.qos.logback.core.util.StringUtil;

public class PaymentServiceImpl implements PaymentService {

	private UserRepository userRepository;

	private OrderItemRepository orderItemRepository;

	private OrderRepository orderRepository;

	private CartRepository cartRepository;

	private CartItemRepository cartItemRepository;

	private OrderItemMapper orderItemMapper;

	private OrderMapper orderMapper;
	
	private PaymentMapper paymentMapper;
	
	private PaymentRepository paymentRepository;

	private CouponRepository couponRepository;

	@Override
	public PaymentDto createPayment(UUID orderId, String urlChecking) {
		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy order id này"));
		Payment payment = Payment.builder()
				.amount(order.getTotalAmount())
				.createdAt(LocalDateTime.now())
				.paymentStatus(PaymentStatus.PENDING)
				.paymentMethod("VNPAY")
				.order(order)
				.build();
		order.setPayment(payment);
		orderRepository.save(order);
		PaymentDto paymentDto = paymentMapper.toDTO(payment);
		
		if (StringUtil.isNullOrEmpty(urlChecking))
			throw BadRequestException.message("Url checking trống!");
		
		paymentDto.setUrlPayment(VNPayConfig.createPaymentRedirect(order.getTotalAmount().longValue(), "Đơn thanh toán cho mã đơn: "+order.getId(), urlChecking));
		return paymentDto;
	}

	@Override
	public PaymentDto updatePayment(PaymentRequest paymentRequest) {
		Payment payment = paymentRepository.findById(paymentRequest.getPaymentId()).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy payment id này"));
		if (!StringUtil.isNullOrEmpty(paymentRequest.getTransactionId()))
			payment.setTransactionId(paymentRequest.getTransactionId());
		
		if (paymentRequest.getPaymentData() != null && !paymentRequest.getPaymentData().isEmpty())
			payment.setPaymentData(paymentRequest.getPaymentData());
		
		payment.setPaymentStatus(paymentRequest.getPaymentStatus());
		paymentRepository.save(payment);
		PaymentDto paymentDto = paymentMapper.toDTO(payment);
		return paymentDto;
	}
	

	@Override
	public PaymentDto getPayment(UUID paymentId) {
		Payment payment = paymentRepository.findById(paymentId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy payment id này"));
		PaymentDto paymentDto = paymentMapper.toDTO(payment);
		return paymentDto;
	}

	@Override
	public List<PaymentDto> getPaymentList(UUID userId) {
		userRepository.findById(userId).orElseThrow(
				() -> BadRequestException.message("Không tìm thấy user id này"));
		List<Payment> payments = paymentRepository.findAllByUserId(userId);
		List<PaymentDto> paymentDtos = paymentMapper.toDTOList(payments);
		return paymentDtos;
	}

	

}
