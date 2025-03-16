package com.vinaacademy.platform.feature.cart.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.cart.dto.CartDto;
import com.vinaacademy.platform.feature.cart.dto.CartRequest;
import com.vinaacademy.platform.feature.cart.entity.Cart;
import com.vinaacademy.platform.feature.cart.mapper.CartMapper;
import com.vinaacademy.platform.feature.cart.repository.CartRepository;
import com.vinaacademy.platform.feature.order_payment.entity.Coupon;
import com.vinaacademy.platform.feature.order_payment.repository.CouponRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private CartRepository cartRepository;
	private UserRepository userRepository;
	private CartMapper cartMapper;
	private CouponRepository couponRepository;

	@Override
	public CartDto getCart(UUID userId) {
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy ID của người này"));
		CartDto cartDto = cartMapper.toDTO(cart);
		return cartDto;
	}

	@Override
	public CartDto createCart(CartRequest request) {
		if (cartRepository.existsByUserId(request.getUser_id()))
			throw BadRequestException.message("Cart của ID người dùng này đã tồn tại");

		User userp = userRepository.findById(request.getUser_id())
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy ID người dùng này"));
		Cart cart = Cart.builder().user(userp).coupon(null).build();
		cartRepository.save(cart);
		CartDto cartDto = cartMapper.toDTO(cart);
		return cartDto;
	}

	@Override
	public CartDto updateCart(CartRequest request) {
		Cart cart = cartRepository.findByUserId(request.getUser_id())
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy Cart của ID người dùng này"));
		User userp = userRepository.findById(request.getUser_id())
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy ID người dùng này"));
		Coupon coupon = null;
		if (request.getCoupon_id() != null) {
			coupon = couponRepository.findById(request.getCoupon_id())
					.orElseThrow(() -> BadRequestException.message("Không tìm thấy coupon"));
		}
		cart.setCoupon(coupon);
		cart.setUser(userp);
		cartRepository.save(cart);
		CartDto cartDto = cartMapper.toDTO(cart);
		return cartDto;
	}

	@Override
	public void deleteCart(CartRequest request) {
		Cart cart = cartRepository.findByUserId(request.getUser_id())
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy Cart của ID người dùng này"));
		cartRepository.delete(cart);
	}

}
