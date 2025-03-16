package com.vinaacademy.platform.feature.order_payment;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinaacademy.platform.feature.cart.dto.CartDto;
import com.vinaacademy.platform.feature.cart.dto.CartItemDto;
import com.vinaacademy.platform.feature.cart.dto.CartItemRequest;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.order_payment.dto.OrderDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderRequest;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentDto;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentRequest;
import com.vinaacademy.platform.feature.order_payment.service.OrderItemService;
import com.vinaacademy.platform.feature.order_payment.service.OrderService;
import com.vinaacademy.platform.feature.order_payment.service.PaymentService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payment")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
	private final PaymentService paymentService;
	
	
	@HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PostMapping
    public ApiResponse<PaymentDto> createPayment(@PathVariable UUID orderId, @PathVariable String urlChange) {
        log.debug("Create payment for order "+orderId);
        return ApiResponse.success(paymentService.createPayment(orderId, urlChange));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/list")
    public ApiResponse<List<PaymentDto>> getPaymentList(@PathVariable UUID userId) {
    	log.debug("Get payment list for user "+userId);
    	return ApiResponse.success(paymentService.getPaymentList(userId));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/crud/{paymentId}")
    public ApiResponse<PaymentDto> getPayment(@PathVariable UUID paymentId) {
    	log.debug("Get payment "+paymentId);
    	return ApiResponse.success(paymentService.getPayment(paymentId));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PutMapping
    public ApiResponse<PaymentDto> updateOrder(@RequestBody @Valid PaymentRequest request) {
        log.debug("Payment updated");
        return ApiResponse.success(paymentService.updatePayment(request));
    }
    

}
