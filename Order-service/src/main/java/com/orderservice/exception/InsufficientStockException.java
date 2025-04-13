package com.orderservice.exception;

public class InsufficientStockException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficientStockException(Long productId) {
        super("Insufficient stock for product ID: " + productId);
    }
}