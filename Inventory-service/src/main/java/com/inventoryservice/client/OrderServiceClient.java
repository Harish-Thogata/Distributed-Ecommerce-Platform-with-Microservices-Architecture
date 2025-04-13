package com.inventoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    
    @PutMapping("/api/internal/orders/products/{productId}/stock")
    public void updateProductStock(@PathVariable Long productId, @RequestParam int stockQuantity);
    
}