package com.outreachiq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GPTResponse {

	@JsonProperty("choices")
	private List<Choice> choices;

	@Data
	public static class Choice {
		@JsonProperty("message")
		private Message message;

		@Data
		public static class Message {
			@JsonProperty("content")
			private String content;
		}
	}
}