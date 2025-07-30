package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.service.OllamaService;

@RestController
@RequestMapping("/api/v1")
public class DocumentUploadController {

	private OllamaService ragService;
	
	public DocumentUploadController(OllamaService ragService) {
		this.ragService=ragService;
	}
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
		ragService.processAndStorepdf(file);
		return ResponseEntity.ok().body("PDF uploaded successfully");
	}
	
}
