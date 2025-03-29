package com.vinaacademy.platform.feature.cart;

import com.vinaacademy.platform.feature.cart.dto.CartDto;
import com.vinaacademy.platform.feature.cart.dto.CartItemDto;
import com.vinaacademy.platform.feature.cart.dto.CartItemRequest;
import com.vinaacademy.platform.feature.cart.dto.CartRequest;
import com.vinaacademy.platform.feature.cart.service.CartItemService;
import com.vinaacademy.platform.feature.cart.service.CartService;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
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
@RequestMapping("/api/v1/cart")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {
	private final CartService cartService;
	private final CartItemService cartItemService;
	
	@HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PostMapping
    public ApiResponse<CartDto> createCart(@RequestBody @Valid CartRequest request) {
        log.debug("Cart created");
        return ApiResponse.success(cartService.createCart(request));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping
    public ApiResponse<CartDto> getCart(@PathVariable UUID userId) {
    	log.debug("Get cart for user "+userId);
        return ApiResponse.success(cartService.getCart(userId));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PutMapping
    public ApiResponse<CartDto> updateCart(@RequestBody @Valid CartRequest request) {
        // Only STUDENT can update their courses
        log.debug("CART updated");
        return ApiResponse.success(cartService.updateCart(request));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @GetMapping("/items")
    public ApiResponse<List<CartItemDto>> getListCartItems(@PathVariable UUID userId) {
    	log.debug("Get list cart items for user "+userId);
        return ApiResponse.success(cartService.getCart(userId).getCartItems());
    }
    
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PostMapping("/items")
    public ApiResponse<CartItemDto> createCartItem(@RequestBody @Valid CartItemRequest request) {
        log.debug("Cart item create");
        return ApiResponse.success(cartItemService.addCartItem(request));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @PutMapping("/items")
    public ApiResponse<CartItemDto> updateCartItem(@RequestBody @Valid CartItemRequest request) {
        log.debug("Cart item update");
        return ApiResponse.success(cartItemService.updateCartItem(request));
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> deleteCartItem(@PathVariable Long itemId) {
        log.debug("Cart item delete");
        cartItemService.deleteCartItem(itemId);
        return ApiResponse.success("Xóa item "+itemId +" thanh cong");
    }
    
    @HasAnyRole({AuthConstants.STUDENT_ROLE}) 
    @GetMapping("/items/{itemId}")
    public ApiResponse<CartItemDto> getCartItem(@PathVariable Long itemId) {
    	log.debug("Get 1 cart item");
        return ApiResponse.success(cartItemService.getCartItem(itemId));
    }
    
    
    
}
