package com.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.DTO.ChangePasswordRequest;
import com.app.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final AuthenticationService authService;
	
	@PostMapping("/change-password")
	public void changePassword(@RequestBody ChangePasswordRequest resetPassworddto) {
		authService.changePassword(resetPassworddto);
		
	}
	
	@GetMapping("/me")
	public String userProfile(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String userName= userDetails.getUsername();
		return userName;
	}
	
	
}

	
