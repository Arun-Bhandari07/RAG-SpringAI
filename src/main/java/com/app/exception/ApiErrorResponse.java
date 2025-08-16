package com.app.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
	private String errorMessage;
	private String httpStatusName;
	private int httpStatusCode;
	private String path;
	private LocalDateTime time;
	private Map<String,String> fieldErrors;
	
	public ApiErrorResponse(String errorMessage, String httpStatusName, int httpStatusCode,
            String path, LocalDateTime time, Map<String, String> fieldErrors) {
			this.errorMessage = errorMessage;
			this.httpStatusName = httpStatusName;
			this.httpStatusCode = httpStatusCode;
			this.path = path;
			this.time = time;
			this.fieldErrors = fieldErrors;
}
	public ApiErrorResponse(String errorMessage, String httpStatusName, int httpStatusCode,
            String path, LocalDateTime time) {
			this(errorMessage,httpStatusName,httpStatusCode,path,time,Collections.emptyMap());
}
	
	
	
}
