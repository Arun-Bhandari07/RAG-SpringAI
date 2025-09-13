package com.app.utils;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.DTO.AuthResponse;
import com.app.entities.User;
import com.app.enums.RegistrationType;
import com.app.enums.UserRole;
import com.app.repositories.UserRepository;
import com.app.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {	
	@Value("${app.frontend_url}")
	private String frontendUrl;
	
	private final UserRepository userRepo;
	
	private final ObjectMapper objectMapper;

	private final OAuth2Utils oauth2Utils;
	
	@Lazy
	private  AuthenticationService authService;

	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		OAuth2AuthenticationToken oAuthToken = (OAuth2AuthenticationToken) authentication;
		OAuth2User oAuth2User = (OAuth2User) oAuthToken.getPrincipal();

		String email = oAuth2User.getAttribute("email");
		RegistrationType registrationType = oauth2Utils.getRegistrationType(oAuthToken.getAuthorizedClientRegistrationId());;
		String registrationId = oauth2Utils.getRegistrationId(oAuth2User, registrationType);
		
		Optional<User> dbOauthUser = userRepo.findByRegTypeAndRegId(registrationType,registrationId);
		Optional<User> emailUser = userRepo.findByEmail(email.toLowerCase().trim());
			
		User user;
		
		if(dbOauthUser.isEmpty() && emailUser.isEmpty()) {
			//CASE: When user doesn't exits by regType, id and email : SignUp 
			log.info("New OAuth2 SignUp with email={}, regType={} , regId={}",email.toLowerCase().trim(),registrationType,registrationId);
			user = new User();
			user.setEmail(email.toLowerCase());
			user.setPassword(null);
			user.setRegId(registrationId);
			user.setRegType(registrationType);
			user.setRole(UserRole.ROLE_USER);
			user.setEmailVerified(true);
			userRepo.save(user);
		}else if(dbOauthUser.isEmpty() && emailUser.isPresent()) {
			//CASE: When no regType and regId is present but only email indicates : manual user trying sign with google
			log.warn("Manual user trying to login with oauth2 with email={}",email);
			throw new BadCredentialsException("Please link account manually from user profile");
		}else{
			//CASE: Presence of regId indicates user have signed up previously: LogIn
			log.info("OAuth2 login success");
			user = dbOauthUser.get();	
		}
		
		
		AuthResponse res = authService.handleOAuth2Login( user);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(res));
	}
	

	
}	

