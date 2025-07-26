package com.app.service.exception;

public class DocumentProcessingException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocumentProcessingException(String message) {
		super(message);
	}
	
	public DocumentProcessingException(String message,Throwable cause) {
		super(message,cause);
	}
	
}
