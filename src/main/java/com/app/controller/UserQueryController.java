package com.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.GeminiService;
import com.app.service.OllamaService;

import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ask")
public class UserQueryController {

	private final OllamaService ollamaService;
	private final GeminiService geminiService;

	public UserQueryController(OllamaService ollamaService
			,GeminiService geminiService) {
		this.ollamaService = ollamaService;
		this.geminiService= geminiService;
	}

	@PostMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> askQuestion(@RequestBody @NotBlank String question) {
		return geminiService.askQuestion(question);

	}
}
