package com.app.configurations;

import java.util.Map;
import java.util.UUID;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.service.AIService;

import reactor.core.publisher.Flux;



@Service
public class AIServiceProvider {

	@Autowired
	private Map<String, AIService> aiServicesMap;
	
	@Autowired
	private ChatMemory chatMemory;

	public Flux<String> process(String provider, String prompt) {
		AIService service = aiServicesMap.get(provider.toLowerCase() + "Service");
		
		if (service == null) {
			throw new IllegalArgumentException("AI Provider doesn't exists" + provider.toLowerCase());
		}
		
		Flux<String> response =  service.askQuestion(prompt)
				.filter(chunk->chunk!=null && !chunk.trim().isEmpty())
				.bufferUntil(chunk->chunk.endsWith(".")|| chunk.endsWith("?")|| chunk.endsWith("!"))
				.map(chunks->String.join(" ", chunks))
				.publish()
				.autoConnect(2);
		
		response
				.collectList()
				.doOnNext(responses->{
					String fullResponse = String.join(" ", responses);
//					TODO 
//					chatMemory.save(conversationId, List.of());
				})
				.subscribe();
		return response;
	}
}
