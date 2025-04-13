package com.productservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.ecommerce.common.dto.StockUpdateDTO;
import com.ecommerce.common.events.ProductStockUpdateEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, StockUpdateDTO> stockUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Use ErrorHandlingDeserializer for safe deserialization
        JsonDeserializer<StockUpdateDTO> deserializer = new JsonDeserializer<>(StockUpdateDTO.class);
        deserializer.addTrustedPackages("com.ecommerce.common.dto"); // Trust only DTO package

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockUpdateDTO> stockUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockUpdateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockUpdateConsumerFactory());
        return factory;
    }
    
 // Add new configuration for ProductStockUpdateEvent
    @Bean
    public ConsumerFactory<String, ProductStockUpdateEvent> productStockUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<ProductStockUpdateEvent> deserializer = 
            new JsonDeserializer<>(ProductStockUpdateEvent.class);
        deserializer.addTrustedPackages("com.ecommerce.common.events");

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductStockUpdateEvent> 
        productStockUpdateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductStockUpdateEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productStockUpdateConsumerFactory());
        return factory;
    }
}
