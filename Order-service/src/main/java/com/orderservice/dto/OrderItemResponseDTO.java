package com.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDTO {  // Part of OrderResponseDTO , Represents an individual item in the order response.
	
	private Long productId;     // ID of the product
	
    private String productName; // Name of the product at the time of purchase
    
    private Double unitPrice;   // Price per unit at the time of purchase
    
    private Integer quantity;  // Number of units ordered
    
    private Double totalPrice; // Total price for this item (unitPrice * quantity)

}
