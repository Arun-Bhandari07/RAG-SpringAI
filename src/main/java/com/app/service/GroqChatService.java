package com.app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;


@Service("groqService")
public class GroqChatService implements AIService{

	@Value("spring.ai.openai.chat.model")
	public String openAiChatModel;
	
	private ChatClient chatClient;
	
	private DocumentRetrievalService documentRetrievalService;
	
	public GroqChatService (ChatClient chatClient
			,DocumentRetrievalService documentRetrievalService) {
		this.chatClient=chatClient;
		this.documentRetrievalService = documentRetrievalService;
	}

	@Override
	public Flux<String> askQuestion(String userPrompt) {
		Prompt prompt = documentRetrievalService.retrieveContent(userPrompt);
		return chatClient.prompt(prompt).stream().content();
		
	}

	
	
}
