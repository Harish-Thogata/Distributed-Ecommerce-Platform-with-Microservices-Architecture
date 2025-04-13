package com.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.common.dto.ProductDTO;
import com.orderservice.entity.ProductInfo;
import com.orderservice.repository.ProductInfoRepository;

//InternalProductController.java (Receives product details from Product Service)
@RestController
@RequestMapping("/api/internal/orders")
public class InternalProductController {

	private final ProductInfoRepository productRepo;
	
    private static final Logger logger = LoggerFactory.getLogger(InternalProductController.class);

    // Constructor injection
    public InternalProductController(ProductInfoRepository productRepo) {
        this.productRepo = productRepo;
    }

	@PostMapping("/products")
	public void receiveProduct(@RequestBody ProductDTO dto) {
		ProductInfo product = new ProductInfo();
		product.setProductId(dto.getProductId());
		product.setProductName(dto.getProductName());
		product.setPrice(dto.getPrice());
		// product.setStockQuantity(dto.getStockQuantity()); // Initial stock from Product Service
		productRepo.save(product);
		logger.info("Saved productId={}", dto.getProductId());
	}
	
	@PutMapping("/products/{productId}/stock")
    public void updateProductStock(@PathVariable Long productId, @RequestParam int stockQuantity) {
        ProductInfo product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(stockQuantity);
        productRepo.save(product);
        logger.info("Updated stock for productId={} to {}", productId, stockQuantity);
    }
}
