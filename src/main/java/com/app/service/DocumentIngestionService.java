package com.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.exception.DocumentProcessingException;

@Service
public class DocumentIngestionService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);

	private final VectorStore vectorStore;

	private final int MIN_CHARACTER_FOR_CHUNKING = 300;

	public DocumentIngestionService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	/**
	 * Converts a document(PDF, PPTX or similar text based file) and embeds the data
	 * Finally, the data is added to added to vector database in vector representation
	 * 
	 * 
	 * @param file
	 * @throws DocumentProcessingException if processing fails
	 * 
	 */
	public void processAndStorepdf(MultipartFile file) {

		try (InputStream inputStream = file.getInputStream()) {

			logger.info("Received multipart file :{}", file.getOriginalFilename());
			InputStreamResource resource = new InputStreamResource(inputStream);
			TikaDocumentReader documentReader = new TikaDocumentReader(resource);
			List<Document> documents = documentReader.get();
			logger.debug("Extracted {} raw documents from file", documents.size());

			List<Document> documentWithMetadata = documents.stream().map(doc -> {
				Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
				metadata.put("source", file.getOriginalFilename());
				return new Document(doc.getFormattedContent(), metadata);
			}).collect(Collectors.toList());

			List<Document> chunkedDocuments = chunkDocument(documentWithMetadata);
			logger.debug("Received {} chunks from file", chunkedDocuments.size());

			vectorStore.add(chunkedDocuments);
			logger.info("Successfully added chunks to vector store");

		} catch (Exception ex) {
			logger.error("Failed to process document: {}", file.getOriginalFilename(), ex);
			throw new DocumentProcessingException("Could not process the file:" + file.getOriginalFilename(), ex);
		}
	}

	/**
	 * Takes input as a list of raw document with metadata Chunks if total text is
	 * greater than specified character
	 * 
	 * @param documentWithMetadata
	 * @return
	 */
	public List<Document> chunkDocument(List<Document> documentWithMetadata) {
		TokenTextSplitter splitter = TokenTextSplitter.builder()
				.withChunkSize(512)
				.build();
		
		List<Document> allChunks = new ArrayList<>();
		for (Document doc : documentWithMetadata) {
			if (doc.getText().length() > MIN_CHARACTER_FOR_CHUNKING) {
				allChunks.addAll(splitter.apply(List.of(doc)));
			} else {
				logger.info("Skipping chunking for short documents of length:{}",doc.getText().length());
				allChunks.add(doc);
			}
		}
		return allChunks;
	}

}
