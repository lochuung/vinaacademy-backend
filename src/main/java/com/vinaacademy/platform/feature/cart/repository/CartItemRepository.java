package com.vinaacademy.platform.feature.cart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinaacademy.platform.feature.cart.entity.Cart;
import com.vinaacademy.platform.feature.cart.entity.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepository  extends JpaRepository<CartItem, Long>{
	
	boolean existsByCourseId(UUID courseId);
	boolean existsById(Long Id);
	@Transactional
    void deleteByCart(Cart cart);
}
