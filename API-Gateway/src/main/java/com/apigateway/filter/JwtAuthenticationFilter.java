/*package com.apigateway.filter;

import com.apigateway.service.JWTService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTService jwtService;

    public JwtAuthenticationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // Skip validation for public endpoints
        if (isPublicEndpoint(path)) {
            logger.debug("Skipping JWT validation for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            logger.warn("Authorization token is missing for path: {}", path);
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is missing"));
        }

        logger.debug("Validating token for path: {}", path);*/
        /*return jwtService.validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        // Token is valid: Create authentication object
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                "user", // Principal (can be a username or user ID)
                                null,   // Credentials (null since we're using JWT)
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Roles/authorities
                        );
                        SecurityContext context = new SecurityContextImpl(authToken);
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                    } else {
                        logger.error("Invalid token for path: {}", path);
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Token validation failed for path: {}", path, e);
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed"));
                });  
    }*/
        
        /*return jwtService.validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(null));
                    } else {
                        return fail(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                    }
                })
                .onErrorResume(ex -> 
                fail(exchange, "Token validation error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
    
    

    private Mono<Void> fail(ServerWebExchange exchange, String message, HttpStatus status) {
        logger.error("{}: {}", message, exchange.getRequest().getPath());
        return Mono.error(new ResponseStatusException(status, message));
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui")  // Swagger UI
                || path.startsWith("/v3/api-docs") // OpenAPI documentation
                || path.startsWith("/user-service/v3/api-docs") // User Service OpenAPI documentation
                || path.startsWith("/product-service/v3/api-docs") // Product Service OpenAPI documentation
                || path.startsWith("/inventory-service/v3/api-docs") // Inventory Service OpenAPI documentation
                || path.startsWith("/order-service/v3/api-docs") // Order Service OpenAPI documentation
                || path.startsWith("/payment-service/v3/api-docs") // Order Service OpenAPI documentation
                || path.startsWith("/webjars")     // WebJars for Swagger UI
                || path.startsWith("/swagger-resources") // Swagger resources
                || path.startsWith("/api/user")   // User Service endpoints
                || path.startsWith("/actuator")  // Actuator endpoints
                || path.equals("/favicon.ico"); // Exclude favicon.ico
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}*/

package com.apigateway.filter;

import com.apigateway.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTService jwtService;

    public JwtAuthenticationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // Skip validation for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            return fail(exchange, "Authorization token is missing", HttpStatus.UNAUTHORIZED);
        }

        return jwtService.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return fail(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                    }
                    return jwtService.getClaims(token)
                            .flatMap(claims -> {
                                // Add user info to headers
                                exchange.getRequest().mutate()
                                    .header("X-User-Email", claims.getSubject())
                                    .build();
                                
                                // Create authentication token and set security context
                                UsernamePasswordAuthenticationToken authToken = 
                                    new UsernamePasswordAuthenticationToken(
                                        claims.getSubject(), 
                                        null,
                                        Collections.emptyList() // No roles needed if not used
                                    );
                                
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                            });
                });
    }

    private Mono<Void> processValidToken(String token, ServerWebExchange exchange, WebFilterChain chain) {
        return jwtService.getClaims(token)
                .flatMap(claims -> {
                    String username = claims.getSubject();
                    List<String> roles = claims.get("roles", List.class);
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    
                    // Add user info to headers
                    exchange.getRequest().mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                    // Create authentication token and set security context
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                });
    }

    private Mono<Void> fail(ServerWebExchange exchange, String message, HttpStatus status) {
        logger.error("{}: {}", message, exchange.getRequest().getPath());
        return Mono.error(new ResponseStatusException(status, message));
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui")  // Swagger UI
                || path.startsWith("/v3/api-docs") // OpenAPI documentation
                || path.startsWith("/user-service/v3/api-docs") // User Service OpenAPI documentation
                || path.startsWith("/product-service/v3/api-docs") // Product Service OpenAPI documentation
                || path.startsWith("/inventory-service/v3/api-docs") // Inventory Service OpenAPI documentation
                || path.startsWith("/order-service/v3/api-docs") // Order Service OpenAPI documentation
                || path.startsWith("/payment-service/v3/api-docs") // Order Service OpenAPI documentation
                || path.startsWith("/webjars")     // WebJars for Swagger UI
                || path.startsWith("/swagger-resources") // Swagger resources
                || path.startsWith("/api/user")   // User Service endpoints
                || path.startsWith("/actuator")  // Actuator endpoints
                || path.equals("/favicon.ico"); // Exclude favicon.ico
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}