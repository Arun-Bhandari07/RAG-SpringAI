package com.app;

import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableAsync
@EnableScheduling
@EnableAutoConfiguration(exclude=OpenAiEmbeddingAutoConfiguration.class)
public class RagWithSpringAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagWithSpringAiApplication.class, args);
	}
	

}
