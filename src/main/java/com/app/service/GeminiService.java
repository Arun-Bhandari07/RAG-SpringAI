package com.app.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;

import reactor.core.publisher.Flux;


@Service("geminiService")
public class GeminiService implements AIService {
	
	@Value("${gemini.chat.model}")
	private String geminiChatModel;

	
	private final DocumentRetrievalService retrievalService;
	private Client client;
	

	public GeminiService(DocumentRetrievalService retrievalService
			,Client client) {
		this.retrievalService=retrievalService;
		this.client=client;
	}

	@Override
	public Flux<String> askQuestion(String userPrompt) {
		Prompt finalPrompt = retrievalService.retrieveContent(userPrompt);
		
		ResponseStream <GenerateContentResponse> responseStream = 
				client.models.generateContentStream(geminiChatModel, finalPrompt.getContents(), null);
		
		return Flux.create(sink->{
			responseStream.iterator().forEachRemaining(responseChunk->{
				String chunk = responseChunk.text();
				if(chunk!=null) {
					String[] splittedChunk = chunk.split("\\s+");
					for(String word:splittedChunk) {
						System.out.println(word);
						sink.next(word.trim());
					}
				}
				
			});
			sink.complete();
		});

	}

	
}
