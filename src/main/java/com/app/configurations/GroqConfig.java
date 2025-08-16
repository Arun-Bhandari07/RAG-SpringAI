package com.app.configurations;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqConfig {
	
	@Value("${spring.ai.openai.base-url}")
	public String groqBaseUrl;
	
	@Value("${spring.ai.openai.api-key}")
	public String groqApiKey;
	
	@Value("${spring.ai.openai.chat.model}")
	public String groqChatModel;
	
	private static final double GROQ_TEMPERATURE = 0.7;
	
	@Autowired
	JdbcChatMemoryRepository chatMemoryRepository;
	
	@Bean
	ChatClient groqChatClient(ChatMemory chatMemory) {
		
		OpenAiApi openAiApi = OpenAiApi.builder()
								.apiKey(groqApiKey)
								.baseUrl(groqBaseUrl)
								.build();
		
		OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
										.model(groqChatModel)
										.temperature(GROQ_TEMPERATURE)
										.build();
		
		OpenAiChatModel chatModel = OpenAiChatModel.builder()
				.openAiApi(openAiApi)
				.defaultOptions(chatOptions)
				.build();
		
		ChatClient openAiChatClient = ChatClient.builder(chatModel).build();
		return openAiChatClient;
	}

    @Bean
    ChatMemory chatMemory() {

		ChatMemory chatMemory = MessageWindowChatMemory.builder()
			    .chatMemoryRepository(chatMemoryRepository)
			    .maxMessages(10)
			    .build();
		return chatMemory;

	}
	
	
	
}
	

