package com.outreachiq.service;

import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outreachiq.exception.JsonParsingException;
import com.outreachiq.exception.OpenAIException;
import com.outreachiq.model.GPTRequest;
import com.outreachiq.model.GPTResponse;
import com.outreachiq.model.OutputEmail;
import com.outreachiq.model.ProductInput;

@Service
public class EmailGenerationService {

	private final WebClient openAIWebClient;
	private final ObjectMapper objectMapper;

	private static final String GPT_MODEL = "gpt-3.5-turbo";
	private static final Integer MAX_TOKENS = 500;
	private static final Double TEMPERATURE = 0.7;
	private static final String USER_ROLE = "user";

	@Autowired
	public EmailGenerationService(WebClient openAIWebClient, ObjectMapper objectMapper) {
		this.openAIWebClient = openAIWebClient;
		this.objectMapper = objectMapper;
	}

	public String generateColdOutreachEmail(ProductInput input) {
		try {
			// Create the prompt using the specified format
			String prompt = createPrompt(input);

			// Send request to OpenAI
			GPTResponse gptResponse = sendToOpenAI(prompt);

			// Extract and parse JSON response
			String jsonResponse = extractContentFromResponse(gptResponse);

			// Parse and validate JSON
			OutputEmail emailResponse = parseJsonResponse(jsonResponse);

			// Convert back to formatted string for return
			return formatEmailResponse(emailResponse);

		} catch (WebClientResponseException e) {
			throw new OpenAIException("Failed to communicate with OpenAI API: " + e.getMessage(), e);
		} catch (JsonProcessingException e) {
			throw new JsonParsingException("Failed to process JSON response", e);
		} catch (Exception e) {
			throw new OpenAIException("Unexpected error during email generation: " + e.getMessage(), e);
		}
	}

	private String createPrompt(ProductInput input) {
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

	private GPTResponse sendToOpenAI(String prompt) {
		GPTRequest request = createGPTRequest(prompt);

		return openAIWebClient.post().bodyValue(request).retrieve().bodyToMono(GPTResponse.class)
				.timeout(Duration.ofSeconds(30))
				.onErrorMap(WebClientResponseException.class, this::handleWebClientError).block();
	}

	private GPTRequest createGPTRequest(String prompt) {
		GPTRequest.Message message = new GPTRequest.Message(USER_ROLE, prompt);
		return new GPTRequest(GPT_MODEL, Collections.singletonList(message), MAX_TOKENS, TEMPERATURE);
	}

	private String extractContentFromResponse(GPTResponse response) {
		if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
			throw new OpenAIException("Empty or invalid response from OpenAI");
		}

		GPTResponse.Choice firstChoice = response.getChoices().get(0);
		if (firstChoice == null || firstChoice.getMessage() == null) {
			throw new OpenAIException("Invalid choice structure in OpenAI response");
		}

		String content = firstChoice.getMessage().getContent();
		if (content == null || content.trim().isEmpty()) {
			throw new OpenAIException("Empty content in OpenAI response");
		}

		return content.trim();
	}

	private OutputEmail parseJsonResponse(String jsonResponse) throws JsonProcessingException {
		// Clean the response to extract JSON if there's additional text
		String cleanJson = extractJsonFromResponse(jsonResponse);

		try {
			OutputEmail emailResponse = objectMapper.readValue(cleanJson, OutputEmail.class);

			// Validate required fields
			if (emailResponse.getSubject() == null || emailResponse.getSubject().trim().isEmpty()) {
				throw new JsonParsingException("Missing or empty subject in response");
			}
			if (emailResponse.getBody() == null || emailResponse.getBody().trim().isEmpty()) {
				throw new JsonParsingException("Missing or empty body in response");
			}

			return emailResponse;

		} catch (JsonProcessingException e) {
			throw new JsonParsingException("Failed to parse JSON response: " + e.getMessage(), e);
		}
	}

	private String extractJsonFromResponse(String response) {
		// Find JSON object boundaries
		int startIndex = response.indexOf('{');
		int endIndex = response.lastIndexOf('}');

		if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
			throw new JsonParsingException("No valid JSON object found in response: " + response);
		}

		return response.substring(startIndex, endIndex + 1);
	}

	private String formatEmailResponse(OutputEmail emailResponse) {
		try {
			return objectMapper.writeValueAsString(emailResponse);
		} catch (JsonProcessingException e) {
			throw new JsonParsingException("Failed to format email response", e);
		}
	}

	private Throwable handleWebClientError(WebClientResponseException ex) {
		String errorMessage = String.format("OpenAI API error - Status: %d, Response: %s", ex.getStatusCode().value(),
				ex.getResponseBodyAsString());
		return new OpenAIException(errorMessage, ex);
	}
}