package com.app.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.AIServiceImpl;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/query/v1")
public class UserQueryController {

	private final AIServiceImpl chatService;
	
	@PostMapping(value ="/ask")
	public Flux<String> askQuestion(@RequestBody Map<String,String> request) {
		String extractedQuestion = request.getOrDefault("question","");
		Flux<String> fluxResponse = chatService.askQuestion(extractedQuestion);
		return fluxResponse;
	}
	
		
}
