package com.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderservice.entity.ProductInfo;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {
	
}