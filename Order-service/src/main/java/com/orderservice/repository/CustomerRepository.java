package com.orderservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderservice.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	public Optional<Customer> findByEmail(String email);
	
}
