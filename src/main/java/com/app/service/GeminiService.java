package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.app.configurations.GeminiConfig;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;

import reactor.core.publisher.Flux;

@Profile("prod")
@Service
public class GeminiService{

	@Value("${gemini.chat.model}")
	private String aiModel;

	@Autowired
	GeminiConfig geminiConfig;

	public GeminiService(GeminiConfig geminiConfig) {
		this.geminiConfig = geminiConfig;
	}

	/**
	 * Calls the LLM(Gemini) with user prompts and streams the space separated words
	 * 
	 * @param userPrompt
	 * @return Flux of type String
	 */
	public Flux<String> askQuestion(String userPrompt) {
		ResponseStream<GenerateContentResponse> responseStream = geminiConfig.client().models
				.generateContentStream(aiModel, userPrompt, null);
		return Flux.create(sink -> {
			responseStream.iterator().forEachRemaining(res -> {
				String textChunk = res.text();
				String[] wordStream = textChunk.split("\\s+");
				for (String word : wordStream) {
					System.out.println(word);
					sink.next(word);
				}
			});
			sink.complete();
		});
	}
}
