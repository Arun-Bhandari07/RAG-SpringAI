package com.app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class AIServiceImpl implements AIService{
	
	private final ChatClient chatClient;
	
	private final DocumentRetrievalService documentRetrievalService;
	
	@Override
	public Flux<String> askQuestion(String userPrompt) {
		Prompt prompt = documentRetrievalService.retrieveContent(userPrompt);
		return chatClient
				.prompt(prompt)
				.stream()
				.content();

}
}
