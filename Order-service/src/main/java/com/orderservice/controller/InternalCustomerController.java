package com.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.common.dto.CustomerIdDTO;
import com.orderservice.entity.Customer;
import com.orderservice.repository.CustomerRepository;

//InternalCustomerController.java (Receives customerId from User Service)
@RestController
@RequestMapping("/api/internal/orders")
public class InternalCustomerController {

	private final CustomerRepository customerRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(InternalCustomerController.class);
	
	// Constructor injection
    public InternalCustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

	@PostMapping("/customer")
	public void receiveCustomer(@RequestBody CustomerIdDTO dto) {
		Customer customer = new Customer();
		customer.setCustomerId(dto.getCustomerId());
		customer.setEmail(dto.getEmailId()); 
		customerRepository.save(customer);
		logger.info("Saved customerId={}", dto.getCustomerId());
	}
}