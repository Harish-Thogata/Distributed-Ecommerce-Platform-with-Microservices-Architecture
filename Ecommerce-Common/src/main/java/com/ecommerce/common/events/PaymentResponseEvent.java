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
public class PaymentResponseEvent {
	
    private String orderId;          // The order ID associated with the payment
    
    private String status;           // Payment status (e.g., "SUCCESS", "FAILED")
    
    private String razorpayPaymentId; // Razorpay payment ID (unique identifier for the payment)
    
    private String razorpayOrderId;  // Razorpay order ID (unique identifier for the order)
    
    private Double amount;           // The amount paid
    
    private LocalDateTime timestamp; // Timestamp of the payment event
}