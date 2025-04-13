package com.productservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productservice.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	public boolean existsByProductName(String productName);
	
	public Optional<Product> findById(Long productId);
	
	public List<Product> findByProductNameContainingIgnoreCase(String productName);

    public List<Product> findByCategoryContainingIgnoreCase(String category);

    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

}
