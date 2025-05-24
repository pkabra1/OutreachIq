// EmailGenerationController.java
package com.outreachiq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.outreachiq.model.ProductInput;
import com.outreachiq.service.EmailGenerationService;

import jakarta.validation.Valid;

//@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGenerationController {

	private final EmailGenerationService emailGenerationService;

	@Autowired
	public EmailGenerationController(EmailGenerationService emailGenerationService) {
		this.emailGenerationService = emailGenerationService;
	}

	@PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateColdOutreachEmail(@Valid @RequestBody ProductInput request) {
		String generatedEmail = emailGenerationService.generateColdOutreachEmail(request);
		return ResponseEntity.ok(generatedEmail);
	}

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("{\"status\":\"UP\"}");
	}
}