package com.orderservice.service;

import java.util.List;

import com.ecommerce.common.events.PaymentStatusEvent;
import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
 
public interface OrderService {
	
	// Create a new order
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest, String authToken);
    
    // Fetch order by ID
    public OrderResponseDTO getOrderById(String orderId, String authToken);
    
    // Fetch all orders for a user
    public List<OrderResponseDTO> getOrdersByUser(String authToken);
    
    // Handle payment response from Payment Service
    public void handlePaymentStatus(PaymentStatusEvent event);

}
