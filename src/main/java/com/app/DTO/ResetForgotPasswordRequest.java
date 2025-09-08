package com.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetForgotPasswordRequest {

	 	@NotBlank
	    private String token;

	 	@NotBlank(message = "New password must not be blank")
	    @Size(min = 8, message = "New password must be at least 8 characters long")
	    @Pattern(
	        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
	        message = "New password must include uppercase, lowercase, digit, and special character"
	    )
	    private String newPassword;

	    @NotBlank
	    private String confirmPassword;
}
