package com.orderservice.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequestDTO { // Client sends this to create an order.
	
	@NotEmpty(message = "Order items cannot be empty")
    @Valid // Validate nested objects
    private List<OrderItemRequestDTO> items;

    @NotNull(message = "Shipping address cannot be null")
    @Size(max = 200, message = "Shipping address must be less than 200 characters")
    private String shippingAddress;
}
