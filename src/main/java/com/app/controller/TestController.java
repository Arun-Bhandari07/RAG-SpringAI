package com.app.controller;

import org.apache.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/public")
	public String testPublicUser() {
		return "I am public";
	}
	
	@GetMapping("/users")
	public String dashboard(OAuth2AuthenticationToken authToken) {
		return "Hello this if for user";
	}
	
	@GetMapping("/test")
	public Object testUser(HttpRequest req) {
		OAuth2User user =(OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}
	
	@GetMapping("/admin")
	public String adminDashboard() {
		return "Hello, Admin";
	}
}
