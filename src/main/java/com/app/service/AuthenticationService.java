package com.app.service;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.DTO.AuthResponse;
import com.app.DTO.LoginRequest;
import com.app.DTO.SignUpRequest;
import com.app.entities.User;
import com.app.enums.RegistrationType;
import com.app.enums.UserRole;
import com.app.repositories.UserRepository;
import com.app.utils.JWTUtilities;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
	
	private final  AuthenticationManager authManager;
	
	private final JWTUtilities jwtUtilities ;
	
	private final UserRepository userRepo;
	
	private final EmailService emailService;
	

	public void registerUser(SignUpRequest signUpReq) {
		if(userRepo.existsByEmail(signUpReq.getEmail())){
			throw new BadCredentialsException("Username already Exists");
		}
		User user = new User();
		user.setName(signUpReq.getFullname());
		user.setEmail(signUpReq.getEmail());
		user.setRegType(RegistrationType.LOCAL);
		user.setPassword(new BCryptPasswordEncoder().encode(signUpReq.getPassword()));
		user.setRole(UserRole.ROLE_USER);
		userRepo.save(user);
		emailService.sendVerificationEmail(user);
	}
	
	public AuthResponse login(LoginRequest loginRequest) {
		try {
			//Check for credentials Validation
			Authentication auth = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getEmail()
							,loginRequest.getPassword()));
			
			//Extract user
			CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();
			User user = userDetails.getUser();
			
			//Generate Token 
			String token = jwtUtilities.generateJwtToken(user);
			Date expiryTime = jwtUtilities.extractAllClaims(token).getExpiration();
			Long expiryTimeInMillis = expiryTime.getTime();
			
			//Send AuthReponse with jwt Token
			return new AuthResponse(token,user.getName(),expiryTimeInMillis);
			
		}catch(BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid Username or Password",ex);
		}
		
	}
	
	public AuthResponse handleOAuth2Login(User user) {
		String jwtToken = jwtUtilities.generateJwtToken(user);
		String email = user.getEmail();
		Date expiryTime = jwtUtilities.extractAllClaims(jwtToken).getExpiration();
		Long expiryTimeInMillis = expiryTime.getTime();
		
		AuthResponse res = new AuthResponse(jwtToken,email,expiryTimeInMillis);
		return res;
	}

}
