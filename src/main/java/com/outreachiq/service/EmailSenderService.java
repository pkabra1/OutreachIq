package com.outreachiq.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.outreachiq.model.SendEmailRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

	private final JavaMailSender mailSender;

	public void sendColdEmails(SendEmailRequest request) {
		for (String recipient : request.getRecipients()) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(request.getSenderEmail());
			message.setTo(recipient);
			message.setSubject(request.getSubject());
			message.setText(request.getBody());

			mailSender.send(message);
		}
	}

}
