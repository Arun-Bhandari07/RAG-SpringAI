package com.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.configurations.AIServiceProvider;
import com.app.service.AIService;

import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ask")
public class UserQueryController {

	private AIServiceProvider aiServiceProvider;

	public UserQueryController(AIServiceProvider aiServiceProvider) {
		this.aiServiceProvider = aiServiceProvider;
	}

	@PostMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> askQuestion(@RequestBody @NotBlank String question) {
		return aiServiceProvider.process("groq",question);

	}
}
