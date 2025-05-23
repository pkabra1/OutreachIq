package com.outreachiq.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class ChatGPTConfiguration {

	@Value("${spring.ai.api.key}")
	private String apiKey; // API key for authentication

	// Bean configuration for RestTemplate
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().add("Authorization", "Bearer " + apiKey); // Add API key to request headers
			return execution.execute(request, body); // Proceed with the request
		});
		return restTemplate; // Return configured RestTemplate
	}
}