package com.vinaacademy.platform.feature.order_payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinaacademy.platform.feature.order_payment.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>{

}
