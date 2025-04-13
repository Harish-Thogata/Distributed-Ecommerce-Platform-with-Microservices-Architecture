package com.ecommerce.common.events;

import lombok.Data;

//InventoryUpdateEvent.java (Sent to Inventory Service)
@Data
public class InventoryUpdateEvent {
	
	private Long productId;
	
	private Integer quantityChange; // Negative for deduction
}