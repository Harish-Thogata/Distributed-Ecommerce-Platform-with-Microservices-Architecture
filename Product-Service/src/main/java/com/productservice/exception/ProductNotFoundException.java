package com.productservice.exception;

public class ProductNotFoundException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor with String message
    public ProductNotFoundException(String message) {
        super(message);
    }

    // Constructor with Long productId
    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId);
    }
}
