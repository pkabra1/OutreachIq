package com.outreachiq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outreachiq.model.OutputEmail;
import com.outreachiq.model.ProductInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.referer}")
    private String referer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutputEmail generateEmail(ProductInput input) {
        String prompt = buildPrompt(input);

        // Build message structure
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", referer) // Required by OpenRouter
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            String response = webClient.post()
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);
            String content = jsonNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            JsonNode parsedContent = objectMapper.readTree(content);
            OutputEmail result = new OutputEmail();
            result.setSubject(parsedContent.path("subject").asText());
            result.setBody(parsedContent.path("body").asText());
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            OutputEmail fallback = new OutputEmail();
            fallback.setSubject("Failed to generate subject");
            fallback.setBody("Error communicating with AI service.");
            return fallback;
        }
    }

    private String buildPrompt(ProductInput input) {
        return String.format("""
                Generate a cold outreach email using the following:
                Product Name: %s
                Tagline: %s
                Overview: %s
                Key Features: %s
                Pricing: %s
                Contact Info: %s
                Target Audience: %s
                Tone: %s

                Respond strictly in this JSON format:
                {
                  "subject": "...",
                  "body": "..."
                }
                """,
                input.getProductName(),
                input.getProductTagLine(),
                input.getProductOverview(),
                String.join(", ", input.getKeyFeatures()),
                input.getPricingDetails(),
                input.getContactInfo(),
                input.getTargetAudience(),
                input.getTone());
    }
}
