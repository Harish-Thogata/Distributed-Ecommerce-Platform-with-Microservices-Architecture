package com.userservice.exception;

public class UserNotFoundException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
        super(message);  // Pass the message to the RuntimeException constructor
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);  // Constructor with message and cause
    }

}