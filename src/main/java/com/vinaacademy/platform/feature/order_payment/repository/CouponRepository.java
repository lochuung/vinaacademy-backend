package com.vinaacademy.platform.feature.order_payment.repository;

import com.vinaacademy.platform.feature.order_payment.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID>{

}
