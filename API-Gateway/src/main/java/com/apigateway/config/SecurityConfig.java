package com.apigateway.config;

import com.apigateway.filter.JwtAuthenticationFilter;
import com.apigateway.service.JWTService;


import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JWTService jwtService) {
	    return http
	            .authorizeExchange(exchanges -> exchanges
	            		.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS preflight
	                    .pathMatchers(
	                            "/swagger-ui/**",
	                            "/v3/api-docs/**",
	                            // Add these lines to allow service-specific Swagger paths
	                            "/user-service/v3/api-docs",
	                            "/product-service/v3/api-docs",
	                            "/inventory-service/v3/api-docs",
	                            "/order-service/v3/api-docs",
	                            "/payment-service/v3/api-docs",
	                            "/webjars/**",
	                            "/swagger-resources/**",
	                            "/swagger-ui.html",
	                            "/api/user/**",
	                            "/actuator/**"
	                    ).permitAll()
	                    //.pathMatchers("/api/product/**").authenticated()
	                    .anyExchange().authenticated()
	            )
	            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allow all origins
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Content-Disposition"));
                    return config;
                }))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or Invalid Token")))
                        .accessDeniedHandler((exchange, ex) -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied")))
                )
                .addFilterAt(new JwtAuthenticationFilter(jwtService), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
               