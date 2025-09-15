package com.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class AIServiceImpl implements AIService{
	
	@Value("classpath:/prompts/system-message.st")
	private Resource systemMessage;
	
	@Value("classpath:/prompts/user-message.st")
	private Resource userMessage;
	
	private final ChatClient chatClient;
	
	private final DocumentRetrievalService documentRetrievalService;
	
	@Override
	public Flux<String> askQuestion(String userQuery) {
		//Retrieve Context
		String retrievedContext = documentRetrievalService.retrieveContent(userQuery);
		
		//Get System PromptTemplate
		PromptTemplate systemPromptTemplate = PromptTemplate.builder()
					.resource(systemMessage)
					.build();
		
		//Get User PromptTemplate
		PromptTemplate userPromptTemplate = PromptTemplate.builder()
				.resource(userMessage)
				.build();
		
		//Prepare a list of final message for LLM
		List<Message> messages = List.of(
				systemPromptTemplate.createMessage(),
				userPromptTemplate.createMessage(Map.of("userQuery",userQuery,"retrievedContext",retrievedContext))
				);
		
		//Prepare Final prompt
		Prompt finalPrompt = Prompt.builder()
				.messages(messages)
				.build();
		
		//Make a call to LLM
		return chatClient
				.prompt(finalPrompt)
				.stream()
				.content()
				.onErrorReturn("Service unavailable now, please try again later");	
	}
}
