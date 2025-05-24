package com.outreachiq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outreachiq.model.SendEmailRequest;
import com.outreachiq.service.EmailSenderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailSenderController {

	private final EmailSenderService emailSenderService;

	@PostMapping("/send-emails")
	public ResponseEntity<String> sendEmails(@RequestBody SendEmailRequest request) {
		emailSenderService.sendColdEmails(request);
		return ResponseEntity.ok("Emails sent successfully");
	}

}
