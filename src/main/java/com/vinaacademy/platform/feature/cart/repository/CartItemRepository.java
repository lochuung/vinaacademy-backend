package com.vinaacademy.platform.feature.cart.repository;

import com.vinaacademy.platform.feature.cart.entity.Cart;
import com.vinaacademy.platform.feature.cart.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository  extends JpaRepository<CartItem, Long>{
	
	boolean existsByCourseId(UUID courseId);
	boolean existsById(Long Id);
	@Transactional
    void deleteByCart(Cart cart);
}
