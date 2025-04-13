package com.paymentservice.dto;

import lombok.Data;

@Data
public class TestPaymentRequest {
    private String orderId;      // Order ID from Order Service
    private double amount;       // Payment amount
    private String email;        // Customer email
    private String phone;        // Customer phone
    private String razorpayKeyId; // Razorpay test key ID (e.g., "rzp_test_xxx")
}
