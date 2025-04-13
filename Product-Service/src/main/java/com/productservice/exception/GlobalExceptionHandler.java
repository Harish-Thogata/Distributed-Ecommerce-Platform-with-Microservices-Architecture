package com.productservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.productservice.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFoundException(ProductNotFoundException ex) {
        
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", ex.getMessage());
        
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
	    Map<String, String> errorResponse = new HashMap<>();
	    errorResponse.put("error", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(DuplicateProductException ex) {
        // Create a custom error response
        ErrorResponse errorResponse = new ErrorResponse("Product with this name already exists");
        
        // Return the response with HTTP status 400 (Bad Request) or 409 (Conflict)
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
