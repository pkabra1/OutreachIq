package com.outreachiq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProductInput {

	@NotBlank(message = "Product name is required")
	@JsonProperty("productName")
	private String productName;

	@NotBlank(message = "Product tagline is required")
	@JsonProperty("productTagLine")
	private String productTagLine;

	@NotBlank(message = "Product overview is required")
	@JsonProperty("productOverview")
	private String productOverview;

	@NotEmpty(message = "Key features are required")
	@JsonProperty("keyFeatures")
	private List<String> keyFeatures;

	@NotBlank(message = "Pricing details are required")
	@JsonProperty("pricingDetails")
	private String pricingDetails;

	@NotBlank(message = "Contact info is required")
	@JsonProperty("contactInfo")
	private String contactInfo;

	@NotBlank(message = "Target audience is required")
	@JsonProperty("targetAudience")
	private String targetAudience;

	@NotBlank(message = "Tone is required")
	@JsonProperty("tone")
	private String tone;

//	@JsonProperty("image")
//	private String imageBase64; // optional, for future use

}
