package com.vinaacademy.platform.feature.order_payment;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentDto;
import com.vinaacademy.platform.feature.order_payment.dto.PaymentRequest;
import com.vinaacademy.platform.feature.order_payment.service.PaymentService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
