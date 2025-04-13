package com.productservice.client;

import com.ecommerce.common.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service") // Use Eureka service name
public interface OrderServiceClient {
	
	@PostMapping("/api/internal/orders/products") // Must match Order Service endpoint
    public void sendProductDetails(@RequestBody ProductDTO productDTO);
}