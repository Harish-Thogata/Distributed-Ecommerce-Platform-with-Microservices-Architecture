package com.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.common.dto.CustomerIdDTO;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
	
	@PostMapping("/api/internal/orders/customer")
    public void sendCustomerId(@RequestBody CustomerIdDTO customerIdDTO);
    
}