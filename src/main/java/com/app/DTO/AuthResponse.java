package com.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
	
	private String token;
	private String username;
	private Long tokenExpiry;
}
