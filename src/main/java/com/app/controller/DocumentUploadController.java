package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.service.DocumentIngestionService;

@RestController
@RequestMapping("/api/v1")
public class DocumentUploadController {

	private DocumentIngestionService ingestionService;
	
	public DocumentUploadController(DocumentIngestionService ingestionService) {
		this.ingestionService=ingestionService;
	}
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
		ingestionService.processAndStorepdf(file);
		return ResponseEntity.ok().body("PDF uploaded successfully");
	}
	
}
