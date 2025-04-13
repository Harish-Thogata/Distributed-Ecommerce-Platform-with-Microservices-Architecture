package com.productservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.common.dto.ProductDTO;
// import com.ecommerce.common.dto.StockUpdateDTO;
import com.productservice.exception.ProductNotFoundException;
import com.productservice.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/product")  // Base URL for all endpoints in this controller
@Tag(name = "Product Service", description = "Product management API")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	// @Autowired
	//private KafkaTemplate<String, StockUpdateDTO> kafkaTemplateStock;
	
	// private static final String STOCK_UPDATE_TOPIC = "inventory-stock-update-topic";
	
	@PostMapping("/addproduct")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDTO) {
        String response = productService.addProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
	
	@PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        String response = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String response = productService.deleteProduct(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@GetMapping("/allproducts")
	    public ResponseEntity<List<ProductDTO>> getAllProducts() {
	        List<ProductDTO> products = productService.getAllProducts();
	        return new ResponseEntity<>(products, HttpStatus.OK);
	    }
	
	@GetMapping("/{productId}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
	    Optional<ProductDTO> product = productService.getProductById(productId);
	    if (product.isPresent()) {
	        return new ResponseEntity<>(product.get(), HttpStatus.OK);
	    } else {
	        throw new ProductNotFoundException("Product not found with ID: " + productId);
	    }
	}
	
	// Search products by name
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> products = productService.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Filter products by category
    @GetMapping("/category")
    public ResponseEntity<List<ProductDTO>> filterByCategory(@RequestParam String category) {
        List<ProductDTO> products = productService.filterByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    // Endpoint to simulate stock update via Kafka (this is triggered by Kafka listener internally)
    /*@PutMapping("/{id}/stock")
    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestParam Integer stockQuantity) {
    	StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(id, stockQuantity);
        kafkaTemplateStock.send(STOCK_UPDATE_TOPIC, stockUpdateDTO);
        return new ResponseEntity<>("Stock update message sent to Kafka", HttpStatus.OK);
    }*/
}
