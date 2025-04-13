package com.orderservice.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ORDERS")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
	@Id
	@NotNull(message = "Order ID cannot be blank")
    private String orderId; // UUID (e.g., "ORD-1234")
	
	@NotNull(message = "User ID cannot be null")
    private Long userId;    // From User Service (JWT)
    
	@NotBlank(message = "Status cannot be blank")
    private String status;  // "PENDING", "CONFIRMED", "CANCELLED"
    
	@NotNull(message = "Total amount cannot be null")
    @Min(value = 0, message = "Total amount must be greater than or equal to 0")
    private Double totalAmount;
    
	@NotBlank(message = "Shipping address cannot be blank")
    @Size(max = 200, message = "Shipping address must be less than 200 characters")
    private String shippingAddress; // Denormalized copy (for historical accuracy)
    
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid // Validate nested OrderItem objects
    private List<OrderItem> items; // List of items in the order
    
    public void calculateTotalAmount() {
        this.totalAmount = this.items.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
    }
}
