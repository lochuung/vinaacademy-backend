package com.vinaacademy.platform.feature.order_payment.repository;

import com.vinaacademy.platform.feature.order_payment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>{

}
