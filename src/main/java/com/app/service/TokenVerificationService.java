package com.app.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.entities.User;
import com.app.entities.VerificationToken;
import com.app.enums.TokenType;
import com.app.repositories.UserRepository;
import com.app.repositories.VerificationTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenVerificationService {
	private static final long OTP_EXPIRATION_TIME_IN_MINUTES = 5;
	
	private final VerificationTokenRepository verificationTokenRepo;
	
	private final UserRepository userRepo;
	
	public String generateAndSaveVerificationToken(String email,TokenType tokenType) {
		User user = userRepo.findByEmail(email)
				.orElseThrow(()->  new IllegalArgumentException("User with email "+email+ "doesn't exsts"));
		String verificationToken = generateVerificationToken();
		VerificationToken verificationRecord = verificationTokenRepo.findByUserAndTokenType(user,tokenType).orElse(new VerificationToken());
		verificationRecord.setToken(verificationToken);
		verificationRecord.setCreatedAt(LocalDateTime.now());
		verificationRecord.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME_IN_MINUTES));
		verificationRecord.setUsed(false);
		verificationRecord.setUser(user);
		verificationRecord.setTokenType(tokenType);
		verificationTokenRepo.save(verificationRecord);
		log.info("New token added for verification of unverified user with id:{} and mail:{}",user.getId(),user.getEmail());
		return verificationToken;
	}
	
	@Transactional
	public boolean verifyOtpAndSaveUser(String token) {
		VerificationToken verificationRecord = verificationTokenRepo.findByToken(token).orElse(null);
		
		if(verificationRecord == null) {
			return false;
		}
		
		if(verificationRecord.getExpiresAt().isBefore(LocalDateTime.now()) 
				|| verificationRecord.isUsed() ) {
			log.info("Expired or Invalid Token : {}",token);
			return false;
		}
		
		verificationRecord.setUsed(true);
		verificationTokenRepo.save(verificationRecord);
		log.info("OTP token used :{} and db updated",token);
		
		User user = verificationRecord.getUser();
		user.setEmailVerified(true);
		userRepo.save(user);
		log.info("User with id {} updated to email verified",user.getId());
		return true;
	}
	
	public String generateVerificationToken() {
		return UUID.randomUUID().toString();
	}
}
