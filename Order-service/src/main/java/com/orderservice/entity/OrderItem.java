package com.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ORDERED_ITEMS")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	
	 @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id; // Database-generated ID (for uniqueness)
    
	 @NotNull(message = "Product ID cannot be null")
	 @Column(name = "product_id", nullable = false)
	 private Long productId; // Store productId instead of Product entity       // From Product Service
    
	 @Column(name = "product_Name", nullable = false)
     private String productName;  // Denormalized (snapshot at order time)
    
	 @NotNull(message = "Quantity cannot be null")
	 @Min(value = 1, message = "Quantity must be at least 1")
     @Column(name = "quantity", nullable = false)
     private Integer quantity;
    
	 @NotNull(message = "Unit price cannot be null")
	 @Min(value = 0, message = "Unit price must be greater than or equal to 0")
	 @Column(name = "unit_price", nullable = false)
	 private Double unit_price; // The price of one unit of the product

	 @Column(name = "total_price", nullable = false)
	 private Double totalPrice; // Total price for this item (unitPrice * quantity)
	 
	 @ManyToOne
     @JoinColumn(name = "order_id", nullable = false)
     private Order order; // Parent order
	 
	 public void calculateTotalPrice() {
		    this.totalPrice = this.unit_price * this.quantity;
	}
}
