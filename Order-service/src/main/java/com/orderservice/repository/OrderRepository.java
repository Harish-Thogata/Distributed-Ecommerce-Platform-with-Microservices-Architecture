package com.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
    
    // Fetch order with items by order ID
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    public Optional<Order> findByIdWithItems(@Param("orderId") String orderId);
    
    // Fetch orders with items by user ID
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.userId = :userId")
    public List<Order> findByUserIdWithItems(@Param("userId") Long userId);
    
    // Fetch order by orderId (if orderId is a separate field)
    public Optional<Order> findByOrderId(String orderId);
    
}
