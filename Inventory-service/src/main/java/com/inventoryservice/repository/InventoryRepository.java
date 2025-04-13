package com.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventoryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	
	public Optional<Inventory> findByProductId(Long productId);

}
