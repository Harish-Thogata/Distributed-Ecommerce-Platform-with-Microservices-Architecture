package com.inventoryservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryservice.service.InventoryService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Service", description = "Inventory management API")
public class InventoryController {
	
	@Autowired
    private InventoryService inventoryService;

    @PutMapping("/update-stock")
    public ResponseEntity<String> updateStock(@RequestParam Long productId, @RequestParam Integer stockQuantity) {
    	String response = inventoryService.updateStock(productId, stockQuantity);
        return ResponseEntity.ok(response); // Return the message
    }
}
