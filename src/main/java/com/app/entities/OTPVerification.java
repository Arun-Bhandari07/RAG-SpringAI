package com.app.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OTPVerification {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String verificationToken;

	private User user;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime expiresAt;
	
	private boolean isUsed;
	
}
