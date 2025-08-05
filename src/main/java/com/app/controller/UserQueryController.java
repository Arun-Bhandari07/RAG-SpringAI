package com.app.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.configurations.AIServiceProvider;

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
	public Flux<String> askQuestion(@RequestBody  Map<String,String> request) {
		String extractedQuestion = request.getOrDefault("question","");
		Flux<String> fluxResponse=  aiServiceProvider.process("groq",extractedQuestion)
				.filter(chunk->chunk!=null && !chunk.trim().isEmpty())
				.bufferUntil(chunk->chunk.endsWith(".")|| chunk.endsWith("?")|| chunk.endsWith("!"))
				.map(chunks->String.join(" ", chunks))
				.doOnNext(System.out::println);
		return fluxResponse;
	}
}
