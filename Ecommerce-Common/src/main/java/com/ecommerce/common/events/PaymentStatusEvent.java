package com.ecommerce.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusEvent {
	
    private String orderId;          // The order ID associated with the payment
    
    private String status;           // Payment status (e.g., "SUCCESS", "FAILED")
    
    private LocalDateTime timestamp; // Timestamp of the payment event
}