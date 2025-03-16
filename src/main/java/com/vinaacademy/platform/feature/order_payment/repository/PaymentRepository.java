package com.vinaacademy.platform.feature.order_payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinaacademy.platform.feature.order_payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	@Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId")
	List<Payment> findAllByUserId(@Param("userId") UUID userId);

	@Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId ORDER BY p.createdAt DESC")
	List<Payment> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

	@Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId AND p.paymentStatus = :status")
	List<Payment> findAllByUserIdAndPaymentStatus(@Param("userId") UUID userId, @Param("status") String status);

}
