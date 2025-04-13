package com.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//Customer.java (Stores customerId from User Service)
@Entity
@Data
@Table(name = "customers")
public class Customer {
	
    @Id
    private Long customerId; // Received via FeignClient from User Service
    
    @Column(name = "email", nullable = false, unique = true) 
    private String email; // Ensure this field exists
}
