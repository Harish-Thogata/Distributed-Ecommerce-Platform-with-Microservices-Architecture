package com.orderservice.controller;

import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO orderRequest,
            @RequestHeader("Authorization") String authToken) {
        return new ResponseEntity<>(
                orderService.createOrder(orderRequest, authToken),
                HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable String orderId,
            @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, authToken));
    }

    @GetMapping("/users")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser(
            @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(orderService.getOrdersByUser(authToken));
    }
}