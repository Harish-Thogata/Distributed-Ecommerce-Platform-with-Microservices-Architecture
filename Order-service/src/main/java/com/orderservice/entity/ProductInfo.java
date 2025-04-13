package com.orderservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//ProductInfo.java (Stores product details from Product Service)
@Entity
@Data
@Table(name = "product_info")
public class ProductInfo {
	
	@Id
	private Long productId;       // Received via FeignClient from Product Service
	
	private String productName;
	
	private Double price;
	
	private Integer stockQuantity; // Updated via Kafka from Inventory Service
}