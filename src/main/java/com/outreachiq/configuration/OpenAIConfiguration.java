package com.outreachiq.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfiguration {

	@Value("${openai.api.key}")
	private String apiKey;

	@Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
	private String apiUrl;

	@Bean
	public WebClient openAIWebClient() {
		return WebClient.builder().baseUrl(apiUrl).defaultHeader("Authorization", "Bearer " + apiKey)
				.defaultHeader("Content-Type", "application/json").build();
	}
}