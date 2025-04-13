package com.ecommerce.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//OrderCreatedEvent.java (Sent to Payment Service)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {    // Kafka event to trigger payment processing.
	
	private String orderId;    // UUID of the order
	
    private Double amount;     // Total amount to charge
    
    private Long userId;       // ID of the user placing the order
}
