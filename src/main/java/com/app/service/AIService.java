package com.app.service;

import reactor.core.publisher.Flux;

public interface AIService {

	/**
	 * Calls the LLM with user prompts and streams the space separated words
	 * 
	 * @param userPrompt
	 * @return Flux of type String
	 */
	public Flux<String> askQuestion(String userPrompt);
}
