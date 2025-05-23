package com.outreachiq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OutputEmail {

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("body")
	private String body;

}
