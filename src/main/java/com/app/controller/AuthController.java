	package com.app.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.DTO.AuthResponse;
import com.app.DTO.ForgotPasswordRequest;
import com.app.DTO.LoginRequest;
import com.app.DTO.ResetForgotPasswordRequest;
import com.app.DTO.SignUpRequest;
import com.app.service.AuthenticationService;
import com.app.service.TokenVerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {
	
	@Value("${app.frontend_url}")
	private String frontendUrl;
	
	private final AuthenticationService authService;
	
	private final TokenVerificationService tokenService;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		authService.registerUser(signUpRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body("An email verification link has been sent if not already registered");
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		AuthResponse res = authService.login(loginRequest);
		return ResponseEntity.ok().body(res);
	}
	
	@PostMapping("/forgot-password")
	public void forgotPassword(@RequestBody ForgotPasswordRequest forgetPasswordRequest) {
		authService.initiateForgotPassword(forgetPasswordRequest.getEmail());
	}
	
	@PostMapping("/forgot-password/reset")
	public ResponseEntity<?> resetForgottenPassword(@RequestBody ResetForgotPasswordRequest request) {
	    authService.resetForgottenPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
	    return ResponseEntity.ok("Password has been reset successfully");
	}
	
	@GetMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@RequestParam(name="token") String token){
		if(!tokenService.verifyOtpAndSaveUser(token)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.location(URI.create(frontendUrl+"/login?verified=error"))
			.build();
		}
		return ResponseEntity.status(HttpStatus.OK)
		.location(URI.create(frontendUrl+"/login?verified=true"))
		.build();
	}
	
}
