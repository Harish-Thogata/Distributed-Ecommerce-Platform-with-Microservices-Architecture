package com.paymentservice.service;

import com.ecommerce.common.events.OrderCreatedEvent;
import com.ecommerce.common.events.PaymentResponseEvent;
import com.ecommerce.common.events.PaymentStatusEvent;
import com.paymentservice.entity.Payment;
import com.paymentservice.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentStatusEvent> kafkaTemplate;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    // Kafka listener for OrderCreatedEvent
    @KafkaListener(topics = "order-created-topic", groupId = "payment-service-consumer-group")
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        if (paymentRepository.findByOrderId(event.getOrderId()).isPresent()) return;

        Payment payment = Payment.builder()
            .orderId(event.getOrderId())
            .amount(event.getAmount())
            .status("PENDING")
            .createdAt(LocalDateTime.now())
            .build();
        
        payment.generatePaymentId(paymentRepository);
        paymentRepository.save(payment);
    }

    // Get Razorpay key ID
    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }

    // Create Razorpay order
    @Transactional
    public String createRazorpayOrder(String orderId) throws RazorpayException {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Prevent duplicate orders for completed payments
        if ("SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Payment already completed for order: " + orderId);
        }
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", payment.getAmount() * 100);
        orderRequest.put("currency", "INR");
        
        // Shorten the receipt to 40 characters
        String receipt = orderId.substring(0, Math.min(orderId.length(), 34)); // Ensure "ord_" + receipt ≤ 40
        orderRequest.put("receipt", "ord_" + receipt); // e.g., "ord_946e3202-8132-4ae2-ac1b-e7b899"

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        paymentRepository.save(payment);
        
        return razorpayOrder.toString();
    }

    // Handle payment webhook
    @Transactional
    public void handlePaymentWebhook(PaymentResponseEvent event) {
        paymentRepository.findByRazorpayOrderId(event.getRazorpayOrderId())
                .ifPresent(payment -> {
                    payment.setStatus(event.getStatus());
                    payment.setRazorpayPaymentId(event.getRazorpayPaymentId());
                    paymentRepository.save(payment);
                    
                    // Send payment status to Kafka
                    kafkaTemplate.send("payment-status-topic", 
                        PaymentStatusEvent.builder()
                            .orderId(payment.getOrderId())
                            .status(payment.getStatus())
                            .build()
                    );
                });
    }
}