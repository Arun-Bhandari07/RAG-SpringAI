package com.app.utils;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.app.enums.RegistrationType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2Utils {

	public RegistrationType getRegistrationType(String clientRegistrationId) {
		return switch(clientRegistrationId.toLowerCase()) {
		case "google" -> RegistrationType.GOOGLE;
		default -> throw new IllegalArgumentException("Unknown Registration Type "+clientRegistrationId.toLowerCase());
		};
	}
	
	public String getRegistrationId(OAuth2User user,RegistrationType
			registrationType) {
		String registrationId = switch(registrationType.toString().toLowerCase()) {
		case "google" -> user.getAttribute("sub");
		default -> throw new IllegalArgumentException("Invalid Registration Id ");
		};
		if(registrationId == null || registrationId.isBlank()) {
			log.error("No registration id found for client {}",registrationType);
			throw new IllegalArgumentException("Unique registration id is not present");
		}
		return registrationId;
		};
	}
