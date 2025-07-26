package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.RagService;

@RestController
@RequestMapping("/ask")
public class UserController {

	private final RagService ragService;
	
	public UserController(RagService ragService) {
		this.ragService = ragService;
	}
	
	@PostMapping("/")
	public ResponseEntity<String> askQuestion(@RequestParam("question") String question) {
		String response = ragService.askQuestion(question);
		return ResponseEntity.ok().body(response);
	}
}
