package com.app.service;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.app.DTO.AuthResponse;
import com.app.DTO.LoginRequest;
import com.app.DTO.SignUpRequest;
import com.app.configurations.SecurityConfig;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import com.app.utils.JWTUtilities;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final  AuthenticationManager authManager;
	
	private JWTUtilities jwtUtilities = new JWTUtilities();
	
	private final UserRepository userRepo;
	
	private final SecurityConfig securityConfig;

	@Transactional
	public void registerUser(SignUpRequest signUpReq) {
		if(userRepo.existsByUsername(signUpReq.getUsername())|| userRepo.existsByEmail(signUpReq.getEmail())){
			throw new BadCredentialsException("Username already Exists");
		}
		User user = new User();
		user.setName(signUpReq.getFullname());
		user.setUsername(signUpReq.getUsername());
		user.setEmail(signUpReq.getEmail());
		user.setPassword(securityConfig.passwordEncoder().encode(signUpReq.getPassword()));
		userRepo.save(user);
	}
	
	public AuthResponse login(LoginRequest loginRequest) {
		try {
			//Check for credentials Validation
			Authentication auth = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUserName()
							,loginRequest.getPassword()));
			
			//Extract user
			CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();
			User user = userDetails.getUser();
			
			//Generate Token 
			String token = jwtUtilities.generateJwtToken(user.getUsername());
			Date expiryTime = jwtUtilities.extractAllClaims(token).getExpiration();
			Long expiryTimeInMillis = expiryTime.getTime();
			
			//Send AuthReponse with jwt Token
			return new AuthResponse(token,user.getUsername(),expiryTimeInMillis);
			
		}catch(BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid Username or Password",ex);
		}
		
	}
}
