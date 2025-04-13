package com.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests(auth -> auth
                // Allow public access to Swagger and API docs
                .requestMatchers(
                    "/user-service/v3/api-docs", // OpenAPI documentation
                    // "/user-service/swagger-ui/**", // Swagger UI
                    "/swagger-ui/**", // Swagger UI
                    "/v3/api-docs/**", // OpenAPI documentation
                    "/webjars/**", // WebJars for Swagger UI
                    "/swagger-resources/**", // Swagger resources
                    "/swagger-ui.html", // Swagger UI HTML
                    "/actuator/**",
                    "/api/user/**"
                ).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
