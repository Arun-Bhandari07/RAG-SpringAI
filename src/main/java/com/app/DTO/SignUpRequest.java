package com.app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
	
	@NotBlank
	@Size(min=3,max=30)
	private String fullname;
	
	@Email
	private String email;
	
	@NotBlank
	@Size(min=3,max=10)
	private String username;
	
	@NotBlank
	@Size(min=8,max=20)
	private String password;
}
