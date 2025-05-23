package com.outreachiq.model;

import lombok.Data;

@Data
public class LeadRequest {

	private String targetIndustry;
	private int numberOfLeads = 3;

}
