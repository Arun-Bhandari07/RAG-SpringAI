package com.app.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(DocumentProcessingException.class)
	public ResponseEntity<ApiErrorResponse> handleDocumentProcessingException(DocumentProcessingException ex,WebRequest req) {
		logger.error(ex.getMessage(),ex);
		ApiErrorResponse response = new ApiErrorResponse(
				ex.getMessage(),
				HttpStatus.UNPROCESSABLE_ENTITY.name(),
				HttpStatus.UNPROCESSABLE_ENTITY.value(),
				req.getDescription(false).substring(4),
				LocalDateTime.now()
				);
		
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
			,BindingResult bindingResult,WebRequest req){
		
		logger.debug(ex.getLocalizedMessage());
		
		Map<String,String> errorsMap = new HashMap<>();
		
		if(bindingResult.hasErrors()) {
			bindingResult.getFieldErrors()
			.forEach(error-> 
			errorsMap.put(error.getField(), error.getDefaultMessage()));
		}
		ApiErrorResponse response = new ApiErrorResponse(
				"Invalid Fields Value",
				HttpStatus.BAD_REQUEST.name(),
				HttpStatus.BAD_REQUEST.value(),
				req.getDescription(false).substring(4),
				LocalDateTime.now(),
				errorsMap);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest req){
		ApiErrorResponse response = new ApiErrorResponse(
				ex.getMessage(),
				HttpStatus.BAD_REQUEST.name(),
				HttpStatus.BAD_REQUEST.value(),
				req.getDescription(false).substring(4),
				LocalDateTime.now()
				);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,WebRequest req){
			ApiErrorResponse response = new ApiErrorResponse(
					ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.name(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					req.getDescription(false).substring(4),
					LocalDateTime.now()
					);
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	
}
