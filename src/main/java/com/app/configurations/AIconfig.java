package com.app.configurations;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIconfig {

	@Value("${gemini.api_key}")
	private String geminiApiKey;

	@Value("${gemini.chat.model}")
	private String geminiChatModel;

	@Value("${gemini.api.base_url}")
	private String geminiBaseUrl;

	@Value("${groq.api_key}")
	private String groqApiKey;

	@Value("${groq.chat.model}")
	private String groqChatModel;

	@Value("${groq.api.base_url}")
	private String groqBaseUrl;

	@ConditionalOnProperty(name="ai.provider",havingValue="gemini")
	@Bean("geminiChatClient")
	public ChatClient geminiChatClient(ChatMemory chatMemory) {
		OpenAiApi geminiApi = OpenAiApi.builder()
				.apiKey(geminiApiKey)
				.baseUrl(geminiBaseUrl)
				.build();
		OpenAiChatOptions geminiChatOptions = OpenAiChatOptions.builder()
				.model(geminiChatModel)
				.build();
		OpenAiChatModel geminiChatModel = OpenAiChatModel.builder()
				.openAiApi(geminiApi)
				.defaultOptions(geminiChatOptions)
				.build();
		
		return ChatClient
				.builder(geminiChatModel)
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
				.build();

	}

	@ConditionalOnProperty(name="ai.provider",havingValue="groq")
	@Bean("groqChatClient")
	public ChatClient groqChatClient(ChatMemory chatMemory) {
		OpenAiApi groqApi = OpenAiApi.builder()
				.apiKey(groqApiKey)
				.baseUrl(groqBaseUrl)
				.build();

		OpenAiChatOptions groqChatOptions = OpenAiChatOptions.builder()
				.model(groqChatModel)
				.build();

		OpenAiChatModel groqChatModel = OpenAiChatModel.builder()
				.openAiApi(groqApi)
				.defaultOptions(groqChatOptions)
				.build();
				
		
		return ChatClient
				.builder(groqChatModel)
				.defaultAdvisors( MessageChatMemoryAdvisor.builder(chatMemory).build())
				.build();
	}
	

	@Bean
	public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(jdbcChatMemoryRepository)
				.build();
		
	}

}
