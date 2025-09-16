package com.app.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

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
			,WebRequest req){
		
		logger.debug(ex.getLocalizedMessage());
		BindingResult bindingResult = ex.getBindingResult();
		Map<String,String> errorsMap = new HashMap<>();

			bindingResult.getFieldErrors()
			.forEach(error-> 
			errorsMap.put(error.getField(), error.getDefaultMessage()));
			
			logger.debug(errorsMap.toString());
			
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
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest req) {
	    	ApiErrorResponse res = new ApiErrorResponse(
	    							ex.getMessage(),
	    							HttpStatus.UNAUTHORIZED.name(),
	    							HttpStatus.UNAUTHORIZED.value(),
	    							req.getDescription(false).substring(4),
	    							LocalDateTime.now()
	    						);
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex, WebRequest req) {
	    ApiErrorResponse res = new ApiErrorResponse(
	            "Route not found: " + ex.getRequestURL(),
	            HttpStatus.NOT_FOUND.name(),
	            HttpStatus.NOT_FOUND.value(),
	            req.getDescription(false).substring(4),
	            LocalDateTime.now()
	    );
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
	}

	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,WebRequest req){
		logger.error("Exception  from : {}",ex.getMessage(),ex);
			ApiErrorResponse response = new ApiErrorResponse(
					"An error occured",
					HttpStatus.INTERNAL_SERVER_ERROR.name(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					req.getDescription(false).substring(4),
					LocalDateTime.now()
					);
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	
}
