package com.app.entities;

import java.time.LocalDateTime;

import com.app.enums.TokenType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class VerificationToken {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String token;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id",nullable=false)
	private User user;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime expiresAt;
	
	private boolean isUsed;
	
	@Enumerated(EnumType.STRING)
	private TokenType tokenType;
	
}
