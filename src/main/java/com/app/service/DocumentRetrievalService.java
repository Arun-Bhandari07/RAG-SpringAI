package com.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class DocumentRetrievalService {
	private static final Logger logger = LoggerFactory.getLogger(DocumentRetrievalService.class);
	
	private VectorStore vectorStore;
	
	public DocumentRetrievalService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}
	/**
	 * Performs similarity search on given prompt and retrieves list of document
	 * 
	 * @param userPrompt
	 * @return userPrompt together with structured prompt for calling LLM.
	 */
	public Prompt retrieveContent(String userPrompt) {
		List<Document> retrievedDocs = vectorStore.similaritySearch(userPrompt);
		logger.info("Found {} similar document on similarity Search",retrievedDocs.size());
		
		String content = retrievedDocs.stream()
				.map(Document::getText)
				.collect(Collectors.joining("\n\n"));

		String promptText = """
				    Use the following documents to answer the question. If the question doesn't make sense, reply generically.
					Also, try not to go way beyond the pdf.
				    Documents:
				    %s

				    Question: %s
				"""
				.formatted(content, userPrompt);

		Prompt prompt = new Prompt(new UserMessage(promptText));
		return prompt;
	}
	
}
