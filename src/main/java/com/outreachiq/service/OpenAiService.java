package com.outreachiq.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outreachiq.model.OutputEmail;
import com.outreachiq.model.ProductInput;

import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
public class OpenAiService {

	@Value("${spring.ai.api.key}")
	private String apiKey; // API key for authenticating requests

	@Value("${spring.ai.endpoint}")
	private String apiEndpoint; // Endpoint URL for the chat model API

	private final RestTemplate restTemplate;

//	private final ChatClient chatClient;
//	private final ObjectMapper objectMapper = new ObjectMapper();

	public OutputEmail generateEmail(ProductInput input) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON); // Set content type to JSON
		headers.set("Authorization", "Bearer " + apiKey); // Set API key in the Authorization header
		String promptText = buildPrompt(input);

		// Creating request body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", "gpt-3.5-turbo"); // Specify the model
		requestBody.put("messages", Collections.singletonList(Map.of("role", "user", "content", input) // Set user input
																										// as message
		));
		requestBody.put("temperature", 1); // Control response randomness
//		requestBody.put("max_tokens", 150); // Limit the response length
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers); // Create HTTP entity with
																							// request body and headers
		ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class); // Send POST
																											// request
																											// to the
																											// API

		// Process and return the response
		return parseResponse(response.getBody()); // Extract and return response text

	}

	// Parse the response JSON to extract text
	private OutputEmail parseResponse(String responseBody) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode json = mapper.readTree(responseBody); // Parse JSON response
			OutputEmail result = new OutputEmail();
			result.setSubject(json.path("subject").asText());
			result.setBody(json.path("body").asText());
			return result;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			OutputEmail fallback = new OutputEmail();
			fallback.setSubject("Could not parse AI response.");
			fallback.setBody("Something wet wrong. Kindly try again in a few minutes."); // return raw content
			return fallback;
		}
	}

	private String buildPrompt(ProductInput input) {
		return String.format("""
				    Generate a cold outreach email for a new product using the following:

				    Product Name: %s
				    Tagline: %s
				    Overview: %s
				    Features: %s
				    Pricing: %s
				    Contact Info: %s
				    Target Audience: %s
				    Tone: %s

				    Return ONLY this JSON format:
				    {
				      "subject": "Your email subject",
				      "body": "Your email body text"
				    }
				""", input.getProductName(), input.getProductTagLine(), input.getProductOverview(),
				String.join(", ", input.getKeyFeatures()), input.getPricingDetails(), input.getContactInfo(),
				input.getTargetAudience(), input.getTone());
	}
}
