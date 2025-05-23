package com.outreachiq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GPTRequest {

	@JsonProperty("model")
	private String model;

	@JsonProperty("messages")
	private List<Message> messages;

	@JsonProperty("max_tokens")
	private Integer maxTokens;

	@JsonProperty("temperature")
	private Double temperature;

	public GPTRequest() {
		super();
	}

	public GPTRequest(String model, List<Message> messages, Integer maxTokens, Double temperature) {
		super();
		this.model = model;
		this.messages = messages;
		this.maxTokens = maxTokens;
		this.temperature = temperature;
	}

	@Data
	public static class Message {
		@JsonProperty("role")
		private String role;

		@JsonProperty("content")
		private String content;

		public Message() {
			super();
		}

		public Message(String role, String content) {
			super();
			this.role = role;
			this.content = content;
		}

	}
}