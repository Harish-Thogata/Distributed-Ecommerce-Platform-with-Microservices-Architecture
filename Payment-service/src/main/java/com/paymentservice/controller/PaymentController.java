package com.paymentservice.controller;

import com.ecommerce.common.events.PaymentResponseEvent;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @GetMapping("/initiate")
    public String initiatePayment(
            @RequestParam String orderId,
            RedirectAttributes redirectAttributes,
            Model model) {

        return paymentRepository.findByOrderId(orderId)
                .map(payment -> {
                	if ("SUCCESS".equals(payment.getStatus())) {
                        // Payment already completed
                        model.addAttribute("message", "Payment already completed for this order!");
                        model.addAttribute("status", "SUCCESS");
                        return "payment"; // Reuse payment.html with a message
                    }
                	
                    model.addAttribute("orderId", payment.getOrderId());
                    model.addAttribute("amount", payment.getAmount());
                    model.addAttribute("razorpayKey", paymentService.getRazorpayKeyId());
                    model.addAttribute("status", "PENDING"); // Add status to model
                    return "payment"; // Renders payment.html
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Order not found");
                    return "redirect:/api/payment/error";
                });
    }
    
    @GetMapping("/success")
    public String paymentSuccess(Model model) {
        model.addAttribute("message", "Payment successful! Check your account for orders.");
        return "payment-success"; // Renders payment-success.html
    }

    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<?> createRazorpayOrder(@RequestParam String orderId) {
        try {
            String razorpayOrder = paymentService.createRazorpayOrder(orderId);
            return ResponseEntity.ok(razorpayOrder);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create Razorpay order: " + e.getMessage());
        }catch (RuntimeException e) {
            // Handle duplicate payment error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    @ResponseBody
    public void handleWebhook(@RequestBody PaymentResponseEvent event) {
        paymentService.handlePaymentWebhook(event);
    }
}