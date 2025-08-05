package com.app;

import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.app.service.GroqChatService;

import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableAutoConfiguration(exclude = OpenAiEmbeddingAutoConfiguration.class)
public class RagWithSpringAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagWithSpringAiApplication.class, args);
	}

}
