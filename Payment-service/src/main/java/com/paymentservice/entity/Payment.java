package com.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;

import com.paymentservice.repository.PaymentRepository;

@Entity
@Data
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentId; // New field for the 3-digit payment ID

    private String orderId;
    
    private Double amount;

    private String status; // Payment status (e.g., PENDING, SUCCESS, FAILED)
    
    @Column(unique = true)
    private String razorpayOrderId;
    
    private String razorpayPaymentId;
    
    private LocalDateTime createdAt;

    // Method to generate a unique 3-digit payment ID
    public void generatePaymentId(PaymentRepository paymentRepository) {
        Random random = new Random();
        String newPaymentId;
        do {
            newPaymentId = String.format("%03d", random.nextInt(1000)); // Generates a 3-digit number
        } while (paymentRepository.existsByPaymentId(newPaymentId)); // Check if the ID already exists
        this.paymentId = newPaymentId;
    }
}
