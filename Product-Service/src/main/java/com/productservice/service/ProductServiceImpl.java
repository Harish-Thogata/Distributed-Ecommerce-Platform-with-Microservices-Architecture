package com.productservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.common.dto.ProductDTO;
import com.ecommerce.common.dto.StockUpdateDTO;
import com.ecommerce.common.events.ProductStockUpdateEvent;
import com.productservice.client.OrderServiceClient;
import com.productservice.constants.KafkaConstants;
import com.productservice.entity.Product;
import com.productservice.exception.DuplicateProductException;
import com.productservice.exception.ProductNotFoundException;
import com.productservice.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{
	
	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
	
	@Autowired
    private ProductRepository productRepository;
	
	@Autowired
    private KafkaTemplate<String, Long> kafkaTemplateInventory;  // Sends productId (Long)
	
	@Autowired
    private OrderServiceClient orderServiceClient; // FeignClient

    /*@Autowired
    private KafkaTemplate<String, ProductDTO> kafkaTemplateOrder; // Sends ProductDTO*/

	@Override
	public String addProduct(ProductDTO productDTO) {
		// Check for existing product before creation
	    if (productRepository.existsByProductName(productDTO.getProductName())) {
	        throw new DuplicateProductException("Product with name '" + 
	            productDTO.getProductName() + "' already exists");
	    }
	    
		Product product = mapToEntity(productDTO);
		
		try {
	        product = productRepository.save(product);
	    } catch (DataIntegrityViolationException ex) {
	        // Handle concurrent duplicate insertion attempts
	        throw new DuplicateProductException("Product already exists");
	    }
		
		// product = productRepository.save(product);
		
		// Update the DTO with the generated productId
	    productDTO.setProductId(product.getProductId());
		
		// Send productId to Inventory Service via Kafka 
        kafkaTemplateInventory.send(KafkaConstants.PRODUCT_INVENTORY_TOPIC, product.getProductId());
        logger.debug("Sent productId={} to topic={}", product.getProductId(), KafkaConstants.PRODUCT_INVENTORY_TOPIC);

        // Send full product details to Order Service
        /*kafkaTemplateOrder.send(KafkaConstants.PRODUCT_ORDER_TOPIC, productDTO);
        logger.info("Product details sent to Order Service: {}", productDTO);*/
        
        // Send product details to Order Service via FeignClient
        try {
            orderServiceClient.sendProductDetails(productDTO); // Feign call
            logger.info("Product details sent to Order Service: {}", productDTO);
        } catch (Exception e) {
            logger.error("Failed to send product details to Order Service: {}", e.getMessage());
            throw new RuntimeException("Order Service communication failed");
        }

        return "Product added successfully with ID: " + product.getProductId();
	}

	@Override
	public String updateProduct(Long productId, ProductDTO productDTO) {
		if (productDTO == null) {
	        throw new IllegalArgumentException("Product details cannot be null.");
	    }

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

	    product.setProductName(productDTO.getProductName());
	    product.setPrice(productDTO.getPrice());
	    product.setCategory(productDTO.getCategory());

	    productRepository.save(product);
	    logger.info("Product updated successfully with ID: {}", productId);
	    
	    return "Product updated successfully with ID: " + productId;
	}

	@Override
	public String deleteProduct(Long productId) {
		if (!productRepository.existsById(productId)) {
	        throw new ProductNotFoundException("Product not found with ID: " + productId);
	    }
	    productRepository.deleteById(productId);
	    logger.info("Product deleted successfully with ID: {}", productId);
	    
	    return "Product deleted successfully with ID: " + productId;
	}
	
	@Override
	public List<ProductDTO> getAllProducts() {
		List<Product> allproducts = productRepository.findAll();
		return allproducts.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<ProductDTO> getProductById(Long productId) {
		 Optional<Product> product = productRepository.findById(productId);
		 return product.map(this::mapToDTO); // Convert Entity to DTO
	}

	@Override
	public List<ProductDTO> searchProducts(String keyword) {
		List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);
		logger.info("Searching for products with keyword: {}", keyword);
	    return products.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<ProductDTO> filterByCategory(String category) {
		List<Product> products = productRepository.findByCategoryContainingIgnoreCase(category);
		logger.info("Filtering products by category: {}", category);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<ProductDTO> filterByPriceRange(Double minPrice, Double maxPrice) {
		List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
		logger.info("Filtering products by price range: {} - {}", minPrice, maxPrice);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// Kafka Listener for Stock Updates from Inventory Service
	@Override
	@KafkaListener(
		    topics = KafkaConstants.INVENTORY_STOCK_UPDATE_TOPIC,
		    groupId = KafkaConstants.PRODUCT_SERVICE_CONSUMER_GROUP,
		    containerFactory = "stockUpdateKafkaListenerContainerFactory"
		)
	public void updateStock(StockUpdateDTO stockUpdateDTO) {
		try {
	        logger.info("Received StockUpdateDTO from Kafka: {}", stockUpdateDTO);

	        Long productId = stockUpdateDTO.getProductId();
	        Integer stockQuantity = stockUpdateDTO.getStockQuantity();

	        Product product = productRepository.findById(productId)
	                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

	        product.setStockQuantity(stockQuantity);
	        productRepository.save(product);

	        logger.info("Stock updated for Product ID: {} with Quantity: {}", productId, stockQuantity);
	        
	        System.out.println("Stock updated for Product ID: " + productId + " with Quantity: " + stockQuantity);
	    } catch (Exception e) {
	        logger.error("Error updating stock: {}", e.getMessage(), e);
	    }
		
	}
	
	@KafkaListener(
	        topics = "product-stock-update-topic",
	        groupId = "product-service-consumer-group",
	        containerFactory = "productStockUpdateListenerContainerFactory"
	    )
	@Transactional
	    public void updateProductStock(ProductStockUpdateEvent event) {
	        Product product = productRepository.findById(event.getProductId())
	            .orElseThrow(() -> new ProductNotFoundException(event.getProductId()));

	        product.setStockQuantity(event.getNewStockQuantity());
	        productRepository.save(product);
	    }
	
	// Utility: Convert ProductDTO → Product Entity
    private Product mapToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        // product.setStock(dto.getStock());  stock will be updated via Kafka.
		return product;  
    }
	
	 // Utility: Convert Product Entity → ProductDTO
    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        return dto;
    }
}
