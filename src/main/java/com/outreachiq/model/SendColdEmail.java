package com.outreachiq.model;

import java.util.List;

import lombok.Data;

@Data
public class SendColdEmail {

	private String subject;
	private String body;
	private List<ClientDetails> recipients;
	private String senderEmail;
	private String senderName;

}
