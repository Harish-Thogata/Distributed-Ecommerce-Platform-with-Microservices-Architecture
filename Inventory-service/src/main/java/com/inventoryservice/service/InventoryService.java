package com.inventoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.common.dto.StockUpdateDTO;
import com.ecommerce.common.events.InventoryUpdateEvent;
import com.ecommerce.common.events.ProductStockUpdateEvent;
import com.inventoryservice.client.OrderServiceClient;
import com.inventoryservice.constants.KafkaConstants;
import com.inventoryservice.entity.Inventory;
import com.inventoryservice.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
	
	@Autowired
    private InventoryRepository inventoryRepository;

    private final KafkaTemplate<String, StockUpdateDTO> kafkaTemplateInventoryStock;
    
    private final KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplateProductStock;
    
    private final OrderServiceClient orderServiceClient;
    
    
    
    public InventoryService(InventoryRepository inventoryRepository,
			KafkaTemplate<String, StockUpdateDTO> kafkaTemplateInventoryStock,
			KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplateProductStock,
			OrderServiceClient orderServiceClient) {
		this.inventoryRepository = inventoryRepository;
		this.kafkaTemplateInventoryStock = kafkaTemplateInventoryStock;
		this.kafkaTemplateProductStock = kafkaTemplateProductStock;
		this.orderServiceClient = orderServiceClient;
	}

    
    @KafkaListener(topics = KafkaConstants.PRODUCT_INVENTORY_TOPIC, 
    			   groupId = KafkaConstants.INVENTORY_SERVICE_CONSUMER_GROUP,
    			   containerFactory = "productIdListenerContainerFactory")
    public void addInventoryForNewProduct(Long productId) {
    	
    	logger.debug("Received productId={} from Kafka", productId);
    	
    	System.out.println("Received Product ID from Kafka: " + productId);
    	
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStockQuantity(0); // Default stock
        inventoryRepository.save(inventory);
        
        System.out.println("Inventory saved for Product ID: " + productId);
    }
    
    public String updateStock(Long productId, Integer stockQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        inventory.setStockQuantity(stockQuantity);
        inventoryRepository.save(inventory);

        // Send stock update to Product Service via Kafka
        sendStockUpdateToProductService(productId, stockQuantity);
        
        // Sync the new stock to Order Service
        orderServiceClient.updateProductStock(productId, stockQuantity); 
        
        //return "StockQuantity updated successfully with ID: " + productId + stockQuantity;
        
        return "Stock updated to " + stockQuantity + " for Product ID: " + productId;
    }

    private void sendStockUpdateToProductService(Long productId, Integer stockQuantity) {
    	
    	// Create the StockUpdateDTO
        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO();
        stockUpdateDTO.setProductId(productId);
        stockUpdateDTO.setStockQuantity(stockQuantity);
        
        // Log the DTO being sent
        System.out.println("Sending StockUpdateDTO to Kafka: " + stockUpdateDTO);

        // Send the DTO to Kafka
        kafkaTemplateInventoryStock.send(KafkaConstants.INVENTORY_STOCK_UPDATE_TOPIC, stockUpdateDTO);
    }
    
    @KafkaListener(topics = "inventory-update-topic", groupId = "inventory-service-consumer-group")
    @Transactional
    public void handleInventoryUpdate(InventoryUpdateEvent event) {
    	// 1. Update inventory stock
        Inventory inventory = inventoryRepository.findByProductId(event.getProductId())
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        // inventory.setStockQuantity(inventory.getStockQuantity() + event.getQuantityChange());
        
        // 2. Calculate the new stock quantity
        int updatedStockQuantity = inventory.getStockQuantity() + event.getQuantityChange();
        
        // 3. Update the inventory stock quantity
        inventory.setStockQuantity(updatedStockQuantity);
        inventoryRepository.save(inventory);
        
     // 4. Publish the updated stock quantity to the Product Service
        ProductStockUpdateEvent productStockUpdateEvent = ProductStockUpdateEvent.builder()
            .productId(event.getProductId())
            .newStockQuantity(updatedStockQuantity) // Use the calculated updatedStockQuantity
            .build();

        kafkaTemplateProductStock.send("product-stock-update-topic", productStockUpdateEvent);
        logger.info("Published ProductStockUpdateEvent: {}", productStockUpdateEvent);
    }
    
}
