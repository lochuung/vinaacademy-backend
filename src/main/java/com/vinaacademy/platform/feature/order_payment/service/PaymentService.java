package com.vinaacademy.platform.feature.order_payment.service;

import com.vinaacademy.platform.feature.order_payment.dto.PaymentDto;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentRequest;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
	
	PaymentDto createPayment(UUID orderId, String urlChecking);
	
	PaymentDto updatePayment(PaymentRequest paymentRequest);
		
	PaymentDto getPayment(UUID paymentId);
	
	List<PaymentDto> getPaymentList(UUID userId);

}
