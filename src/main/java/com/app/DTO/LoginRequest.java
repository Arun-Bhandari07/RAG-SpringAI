package com.app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class LoginRequest {

	@NotBlank(message="Email should not be empty")
	@Email(message="Invalid email")
	private String email;
	
	@NotBlank(message="Password should not be empty")
	private String password;

	public String getEmail() {
		return email.toLowerCase().strip();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
