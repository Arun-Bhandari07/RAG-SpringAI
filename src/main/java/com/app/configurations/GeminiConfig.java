package com.app.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.genai.Client;

@Profile("prod")
@Configuration
public class GeminiConfig {

	@Value("${gemini.api.key}")
	private String apiKey;
	
	@Value("${gemini.chat.model}")
	private String geminiModel;
	
	@Bean
	public Client client() {
		return Client.builder()
				.apiKey(apiKey)
				.build();
	}
	

}
