package com.app.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(DocumentProcessingException.class)
	public ResponseEntity<String> handleDocumentProcessingException(DocumentProcessingException ex) {
		logger.error(ex.getMessage(),ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
							.body("Failed to process document: "+ex.getMessage());
	}
}
