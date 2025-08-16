package com.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.configurations.AIServiceProvider;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ask")
public class UserQueryController {

	private AIServiceProvider aiServiceProvider;
	private ChatMemory chatMemory;

	public UserQueryController(AIServiceProvider aiServiceProvider
			,ChatMemory chatMemory) {
		this.aiServiceProvider = aiServiceProvider;
		this.chatMemory=chatMemory;
	}
	
	@PostMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> askQuestion(@RequestBody  Map<String,String> request) {
		String extractedQuestion = request.getOrDefault("question","");
		Flux<String> fluxResponse=  aiServiceProvider.process("gemini",extractedQuestion);
		return fluxResponse;
	}
	
	@GetMapping("/history")
    public List<Message> getHistory() {
        return chatMemory.get("default");
    }
}
