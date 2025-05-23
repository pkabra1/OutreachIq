package com.outreachiq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.outreachiq.model.OutputEmail;
import com.outreachiq.model.ProductInput;
import com.outreachiq.service.OpenAiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

//@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Enable CORS for all origins (for react frontend)
public class EmailController {

	private final OpenAiService openAiService;

	@PostMapping("/generate-email")
	public ResponseEntity<OutputEmail> generateColdEmail(@Valid @RequestBody ProductInput input) {
		OutputEmail result = openAiService.generateEmail(input);
		return ResponseEntity.ok(result);
	}

}
