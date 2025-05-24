package com.outreachiq.model;

import java.util.List;

import lombok.Data;

@Data
public class SendEmailRequest {

	private String senderEmail;
	private String senderName;
	private String subject;
	private String body;
	private List<String> recipients;

}
