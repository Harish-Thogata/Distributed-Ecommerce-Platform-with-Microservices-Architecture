package com.apigateway.config;

import java.util.Collections;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .components(new Components());
	}

	@Bean
	public OpenApiCustomizer customizeOpenApi() {
	    return openApi -> {
	        // Ensure API Gateway does not enforce JWT globally
	        openApi.getPaths().entrySet().stream()
	                .filter(entry -> entry.getKey().startsWith("/api/user") || entry.getKey().startsWith("/v3/api-docs"))
	                .forEach(entry -> entry.getValue().readOperations()
	                        .forEach(operation -> operation.setSecurity(Collections.emptyList())));

	        // Do not apply security at the gateway level for Product Service
	        // Security should be handled inside Product Service's SwaggerConfig
	    };
	}
}


