package com.inventoryservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.ecommerce.common.dto.StockUpdateDTO;
import com.ecommerce.common.events.ProductStockUpdateEvent;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Producer for StockUpdateDTO
    /*@Bean
    public ProducerFactory<String, StockUpdateDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, StockUpdateDTO> kafkaTemplateInventoryStock() {
        return new KafkaTemplate<>(producerFactory());
    }*/
    
    // Producer for StockUpdateDTO
    @Bean
    public ProducerFactory<String, StockUpdateDTO> stockUpdateDTOProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Producer for ProductStockUpdateEvent
    @Bean
    public ProducerFactory<String, ProductStockUpdateEvent> productStockUpdateProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // KafkaTemplate for StockUpdateDTO
    @Bean
    public KafkaTemplate<String, StockUpdateDTO> kafkaTemplateStockUpdateDTO() {
        return new KafkaTemplate<>(stockUpdateDTOProducerFactory());
    }

    // KafkaTemplate for ProductStockUpdateEvent
    @Bean
    public KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplateProductStockUpdate() {
        return new KafkaTemplate<>(productStockUpdateProducerFactory());
    }
}
