package com.vinaacademy.platform.feature.cart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinaacademy.platform.feature.cart.entity.Cart;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	
	 Optional<Cart> findByUserId(UUID userId);
	 
	 boolean existsByUserId(UUID userId);

}
