package com.app.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.exception.DocumentProcessingException;

@Service
public class OllamaService {

	private final VectorStore vectorStore;
	private final OllamaChatModel ollamaChatModel;
	

	public OllamaService(VectorStore vectorStore, OllamaChatModel ollamaChatModel) {

		this.vectorStore = vectorStore;
		this.ollamaChatModel = ollamaChatModel;

	}

	
	/**
	 * Converts a document(.pdf, .pptx or similar text based file and embeds the data 
	 * finally, the data is added to added to vector database in vector format
	 * 
	 * @param file
	 * @throws DocumentProcessingException if processing fails
	 * 
	 */
	public void processAndStorepdf(MultipartFile file) {
		
		try(InputStream inputStream = file.getInputStream()){
		
			InputStreamResource resource = new InputStreamResource(inputStream);
			
			TikaDocumentReader documentReader = new TikaDocumentReader(resource);
			
	
			List<Document> documents = documentReader.get();
			
			List<Document> documentWithMetadata = documents.stream()
												.map(doc->{
												Map<String,Object> metadata = new HashMap<>(doc.getMetadata());
												metadata.put("source", file.getOriginalFilename());
												return new Document(doc.getFormattedContent(),metadata);
											}).collect(Collectors.toList());
		
			vectorStore.add(documentWithMetadata);
			
		}catch(Exception ex) {
			throw new DocumentProcessingException("Could not process the file:"+file.getOriginalFilename(),ex);
	}
	}

	
	/**
	 * Takes userQuery and performs similarity search within database 
	 * @param question
	 * @return
	 */
	public String askQuestion(String userPrompt) {

		List<Document> retrievedDocs = vectorStore.similaritySearch(userPrompt);

		String content = retrievedDocs.stream()
				.map(Document::getText)
				.collect(Collectors.joining("\n\n"));

		String promptText = "Use the following documents to answer the question and if the question doesn't make sense, give generic reply: \n\n" + content + "\n\nQuestion:"
				+ userPrompt;

		Prompt prompt = new Prompt(new UserMessage(promptText));
		return ollamaChatModel.call(prompt).getResult().getOutput().getText();

	}
}
