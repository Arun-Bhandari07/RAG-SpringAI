package com.app.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class OllamaService {

	private final OllamaChatModel ollamaChatModel;
	private final DocumentRetrievalService retrievalService;

	public OllamaService( OllamaChatModel ollamaChatModel
			,DocumentRetrievalService retrievalService) {
		this.ollamaChatModel = ollamaChatModel;
		this.retrievalService=retrievalService;
	}

	
	public Flux<String> askQuestion(String userPrompt) {
		Prompt finalPrompt = retrievalService.retrieveContent(userPrompt);
		return ollamaChatModel.stream(finalPrompt.getContents());

	}

	
}
