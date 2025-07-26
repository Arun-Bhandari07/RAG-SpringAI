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

import com.app.service.exception.DocumentProcessingException;

@Service
public class RagService {
	
	
	private final VectorStore vectorStore;
	private final OllamaChatModel  ollamaChatModel;

	public RagService(VectorStore vectorStore,OllamaChatModel ollamaChatModel) {
		
		this.vectorStore=vectorStore;
		this.ollamaChatModel=ollamaChatModel;
	}
	
	
	
	public void processAndStorepdf(MultipartFile file) {
		try(InputStream inputStream = file.getInputStream()){
			//wrap the inputStream of file in inputStreamResource to instantiate documentReader
			InputStreamResource resource = new InputStreamResource(inputStream);
			
			//Instantiate document reader with InputStreamResource
			TikaDocumentReader documentReader = new TikaDocumentReader(resource);
			
			
			//Extract document returned by document reader
			List<Document> documents = documentReader.get();
			
			List<Document> documentWithMetadata = documents.stream()
											.map(doc->{
												Map<String,Object> metadata = new HashMap<>(doc.getMetadata());
												metadata.put("source", file.getOriginalFilename());
												return new Document(doc.getFormattedContent(),metadata);
											}).collect(Collectors.toList());
		
			//save in vector db
			vectorStore.add(documents);
			
		}catch(Exception ex) {
			throw new DocumentProcessingException("Could not process the file:"+file.getOriginalFilename(),ex);
		}
	}



	public String askQuestion(String question) {
		
			//retrieve document using semantic search
		List<Document> retrievedDocs = vectorStore.similaritySearch(question);
		
		
		//get the content from retrieved documents to pass it to ollama
		String content = retrievedDocs.stream()
						.map(Document::getText)
						.collect(Collectors.joining("\n\n"));
		
		//prepare a prompt for ollama
		String promptText = "Use the following documents to answer the question: \n\n"+content+
				"\n\nQuestion:"+question;
		
		Prompt prompt = new Prompt(new UserMessage(promptText));
		return ollamaChatModel.call(prompt).getResult().getOutput().getText();
		
	}
}
