package com.app.DTO;

public class ForgotPasswordRequest {

	private String email;

	public String getEmail() {
		return email.toLowerCase().strip();
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
