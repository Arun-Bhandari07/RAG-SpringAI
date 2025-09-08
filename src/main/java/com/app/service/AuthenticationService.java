package com.app.service;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.DTO.AuthResponse;
import com.app.DTO.ChangePasswordRequest;
import com.app.DTO.LoginRequest;
import com.app.DTO.SignUpRequest;
import com.app.entities.User;
import com.app.entities.VerificationToken;
import com.app.enums.RegistrationType;
import com.app.enums.UserRole;
import com.app.repositories.UserRepository;
import com.app.repositories.VerificationTokenRepository;
import com.app.utils.JWTUtilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final AuthenticationManager authManager;

	private final JWTUtilities jwtUtilities;

	private final UserRepository userRepo;

	private final MailService mailService;
	
	private PasswordEncoder passwordEncoder;
	
	private VerificationTokenRepository verificationTokenRepo;

	public void registerUser(SignUpRequest signUpReq) {
		if (userRepo.existsByEmail(signUpReq.getEmail())) {
			log.info("User already exists. Can't register for email{}", signUpReq.getEmail());
			throw new BadCredentialsException("Email already registered");
		}
		User user = new User();
		user.setName(signUpReq.getFullname());
		user.setEmail(signUpReq.getEmail());
		user.setRegType(RegistrationType.LOCAL);
		user.setPassword(passwordEncoder.encode(signUpReq.getPassword()));
		user.setRole(UserRole.ROLE_USER);
		user = userRepo.save(user);
		log.info("Registered a user with id :{} and mail: {}", user.getId(), user.getEmail());
		mailService.sendVerificationEmail(user.getEmail());
	}

	public AuthResponse login(LoginRequest loginRequest) {
		log.info("Login request with mail:{}", loginRequest.getEmail());
		try {
			Authentication auth = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
			User user = userDetails.getUser();

			if (!user.isEmailVerified()) {
			    throw new BadCredentialsException("Email not verified. Please verify first.");
			}
			
			String jwtToken = jwtUtilities.generateJwtToken(user);
			Date expiryTime = jwtUtilities.extractAllClaims(jwtToken).getExpiration();
			Long expiryTimeInMillis = expiryTime.getTime();

			return new AuthResponse(jwtToken, user.getEmail(), expiryTimeInMillis);

		} catch (BadCredentialsException ex) {
			log.info("Requested log in with bad credentials. Email:{} ", loginRequest.getEmail());
			throw new BadCredentialsException("Invalid Username or Password", ex);
		}

	}

	public void changePassword(ChangePasswordRequest changePasswordRequest) {
		if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())){
			throw new BadCredentialsException("Confirm password doesn't match");
		}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(!(authentication instanceof AnonymousAuthenticationToken)) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			
			String email = userDetails.getUsername();
			User user = userRepo.findByEmail(email)
						.orElseThrow(()->new IllegalArgumentException("User doesn't exits with give email"));
			 
			if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Current password is incorrect");
			}
			
			user.setPassword(passwordEncoder.encode(changePasswordRequest.getConfirmPassword()));
			userRepo.save(user);
			}
	}

	public void resetForgottenPassword(String token, String newPassword, String confirmPassword) {
	    if (!newPassword.equals(confirmPassword)) {
	        throw new IllegalArgumentException("Passwords do not match");
	    }

	    VerificationToken verificationRecord = verificationTokenRepo.findByToken(token)
	        .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

	    if (verificationRecord.getExpiresAt().isBefore(LocalDateTime.now()) || verificationRecord.isUsed()) {
	        throw new IllegalArgumentException("Token is expired or already used");
	    }

	    User user = verificationRecord.getUser();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepo.save(user);

	    verificationRecord.setUsed(true);
	    verificationTokenRepo.save(verificationRecord);

	    log.info("Password reset for user: {}", user.getEmail());
	}

	
	public AuthResponse handleOAuth2Login(User user) {
		String jwtToken = jwtUtilities.generateJwtToken(user);
		String email = user.getEmail();
		Date expiryTime = jwtUtilities.extractAllClaims(jwtToken).getExpiration();
		Long expiryTimeInMillis = expiryTime.getTime();

		AuthResponse res = new AuthResponse(jwtToken, email, expiryTimeInMillis);
		return res;
	}

	public String initiateForgotPassword(String email) {
		userRepo.findByEmail(email).ifPresent((user) -> {
			mailService.sendForgotPasswordEmail(email);
		});
		return "A mail has been sent to email if the system is registered in the system";
	}

}
