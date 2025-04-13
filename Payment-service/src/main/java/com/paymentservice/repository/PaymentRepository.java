package com.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	
	public boolean existsByPaymentId(String paymentId);
	
	public Optional<Payment> findByOrderId(String orderId);
	
    public Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    
    public Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

}
