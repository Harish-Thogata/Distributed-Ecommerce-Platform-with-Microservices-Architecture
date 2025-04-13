package com.orderservice.service;

import com.ecommerce.common.events.InventoryUpdateEvent;
import com.ecommerce.common.events.OrderCreatedEvent;
import com.ecommerce.common.events.PaymentStatusEvent;
import com.ecommerce.common.events.ProductStockUpdateEvent;
import com.orderservice.dto.*;
import com.orderservice.entity.*;
import com.orderservice.exception.InsufficientStockException;
import com.orderservice.exception.OrderNotFoundException;
import com.orderservice.repository.*;
import com.orderservice.util.JwtUtils;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductInfoRepository productInfoRepository;
    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtUtils jwtUtil;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest, String authToken) {
        // Extract user ID from JWT
        /* Long userId = jwtUtil.extractUserId(authToken.replace("Bearer ", ""));*/
        
        // Validate customer exists
       /* customerRepository.findById(userId)
                .orElseThrow(() -> new OrderNotFoundException("Customer not found with ID: " + userId));*/
       
       // Extract email instead of user ID
       String email = jwtUtil.extractUserEmail(authToken.replace("Bearer ", ""));
       
       // Find customer by email
       Customer customer = customerRepository.findByEmail(email)
               .orElseThrow(() -> new OrderNotFoundException("Customer not found with email: " + email));

        // Create order entity
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(customer.getCustomerId());
        order.setStatus("PENDING");
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());

        // Process order items
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    ProductInfo product = productInfoRepository.findById(item.getProductId())
                            .orElseThrow(() -> new OrderNotFoundException("Product not found: " + item.getProductId()));

                    // Check stock
                    if(product.getStockQuantity() < item.getQuantity()) {
                        throw new InsufficientStockException(product.getProductId());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(product.getProductId());
                    orderItem.setProductName(product.getProductName());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnit_price(product.getPrice());
                    orderItem.calculateTotalPrice();
                    orderItem.setOrder(order);
                    return orderItem;
                }).collect(Collectors.toList());

        order.setItems(orderItems);
        order.calculateTotalAmount();
        
        // Save order
        orderRepository.save(order);

        // Send to Payment Service via Kafka
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getOrderId());
        event.setAmount(order.getTotalAmount());
        event.setUserId(customer.getCustomerId());
        kafkaTemplate.send("order-created-topic", event);

        return mapToOrderResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true) 
    public OrderResponseDTO getOrderById(String orderId, String authToken) {
    	// Extract email from JWT token
        String email = jwtUtil.extractUserEmail(authToken.replace("Bearer ", ""));
        
        // Find customer by email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new OrderNotFoundException("Customer not found with email: " + email));
        
        // Fetch order with items
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        // Verify that the order belongs to the customer
        if (!order.getUserId().equals(customer.getCustomerId())) {
            throw new OrderNotFoundException("Order not found for user");
        }

        return mapToOrderResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUser(String authToken) {
    	// Extract email from JWT
        String email = jwtUtil.extractUserEmail(authToken.replace("Bearer ", ""));

        // Find customer by email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new OrderNotFoundException("Customer not found with email: " + email));
        
        // Fetch orders with items by user ID
        List<Order> orders = orderRepository.findByUserIdWithItems(customer.getCustomerId());
        
        return orders.stream()
                .map(this::mapToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "payment-status-topic", groupId = "order-service-consumer-group")
    @Transactional
    public void handlePaymentStatus(PaymentStatusEvent event) {
        logger.info("Received PaymentStatusEvent: {}", event);
        
        try {
            Order order = orderRepository.findByOrderId(event.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
            
            logger.info("Updating order {} status from {} to {}", 
                    order.getOrderId(), order.getStatus(), event.getStatus());
            
            order.setStatus(event.getStatus());
            orderRepository.save(order);
            
            if ("SUCCESS".equals(event.getStatus())) {
                logger.info("Processing inventory update for order {}", order.getOrderId());
                updateInventory(order);
            }
        } catch (Exception e) {
            logger.error("Failed to process payment status update", e);
            // Consider implementing dead-letter queue here
        }
    }

    private void updateInventory(Order order) {
    	
    	try {
            logger.info("Reducing inventory for order {}", order.getOrderId());
        order.getItems().forEach(item -> {
            InventoryUpdateEvent inventoryEvent = new InventoryUpdateEvent();
            inventoryEvent.setProductId(item.getProductId());
            inventoryEvent.setQuantityChange(-item.getQuantity());
            kafkaTemplate.send("inventory-update-topic", inventoryEvent);
        });
    	}catch (Exception e) {
            logger.error("Inventory update failed", e);
        }
    }
    
    @KafkaListener(
    	    topics = "product-stock-update-topic",
    	    groupId = "order-service-consumer-group"
    	)
    	@Transactional
    	public void handleProductStockUpdate(ProductStockUpdateEvent event) {
    	    logger.info("Received ProductStockUpdateEvent: {}", event);
    	    
    	    productInfoRepository.findById(event.getProductId())
    	        .ifPresentOrElse(
    	            product -> {
    	                product.setStockQuantity(event.getNewStockQuantity());
    	                productInfoRepository.save(product);
    	                logger.info("Updated stock for product {} to {}", 
    	                    event.getProductId(), event.getNewStockQuantity());
    	            },
    	            () -> logger.warn("Product {} not found in Order Service", event.getProductId())
    	        );
    	}

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(this::mapToOrderItemResponseDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .unitPrice(item.getUnit_price())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}