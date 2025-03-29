package com.vinaacademy.platform.feature.order_payment;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.order_payment.dto.OrderDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderRequest;
import com.vinaacademy.platform.feature.order_payment.service.OrderItemService;
import com.vinaacademy.platform.feature.order_payment.service.OrderService;
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
@RequestMapping("/api/v1/order")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
	private final OrderService orderService;
	
	private final OrderItemService orderItemService;
	
	@HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PostMapping
    public ApiResponse<OrderDto> createOrder(@PathVariable UUID userId) {
        log.debug("Order created for user "+userId);
        return ApiResponse.success(orderService.createOrder(userId));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping
    public ApiResponse<OrderDto> getOrder(@PathVariable UUID orderId) {
    	log.debug("Get order "+orderId);
    	return ApiResponse.success(orderService.getOrder(orderId));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteCartItem(@PathVariable UUID orderId) {
        log.debug("Cart item delete");
        orderService.deleteOrder(orderId);
        return ApiResponse.success("Xóa order "+orderId +" thanh cong");
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PutMapping
    public ApiResponse<OrderDto> updateOrder(@RequestBody @Valid OrderRequest request) {
        log.debug("Order updated");
        return ApiResponse.success(orderService.updateOrder(request));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @GetMapping("/crud/items")
    public ApiResponse<List<OrderItemDto>> getListCartItems(@PathVariable UUID orderId) {
    	log.debug("Get list items of order  "+orderId);
        return ApiResponse.success(orderItemService.getOrderItems(orderId));
    }
    
    
   
    
    
}
