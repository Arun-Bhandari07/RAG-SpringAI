package com.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 * @return retrieved content as a string which acts as a context.
	 */
	public String retrieveContent(String userPrompt) {
		List<Document> retrievedDocs = vectorStore.similaritySearch(userPrompt);
		logger.info("Found {} similar document on similarity Search",retrievedDocs.size());
		
		String context = retrievedDocs.stream()
				.map(Document::getText)
				.collect(Collectors.joining("\n\n"));

	
		return context;
	}
	
}
