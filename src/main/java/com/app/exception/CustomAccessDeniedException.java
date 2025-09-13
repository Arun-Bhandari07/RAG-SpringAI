package com.app.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedException implements AccessDeniedHandler {

	  private final ObjectMapper objectMapper = new ObjectMapper();

	    @Override
	    public void handle(HttpServletRequest request,
	                       HttpServletResponse response,
	                       AccessDeniedException accessDeniedException)
	            throws IOException, ServletException {

	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.setContentType("application/json");

	        Map<String, Object> body = new HashMap<>();
	        body.put("status", HttpServletResponse.SC_FORBIDDEN);
	        body.put("error", "Forbidden");
	        body.put("message", accessDeniedException.getMessage());
	        body.put("path", request.getRequestURI());

	        response.getWriter().write(objectMapper.writeValueAsString(body));
	    }
}
