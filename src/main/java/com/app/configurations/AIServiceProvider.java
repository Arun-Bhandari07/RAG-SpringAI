package com.app.configurations;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.service.AIService;

import reactor.core.publisher.Flux;

@Service
public class AIServiceProvider {

	@Autowired
	private Map<String,AIService> aiServicesMap;
	
	public Flux<String> process (String provider,String prompt) {
		AIService service = aiServicesMap.get(provider.toLowerCase()+"Service");
		if(service==null) {
			throw new IllegalArgumentException("AI Provider doesn't exitss"+provider.toLowerCase());
		}
		return service.askQuestion(prompt);
	}
}
