package com.outreachiq.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OpenAIException.class)
	public ResponseEntity<Map<String, String>> handleOpenAIException(OpenAIException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "OpenAI API Error");
		error.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
	}

	@ExceptionHandler(JsonParsingException.class)
	public ResponseEntity<Map<String, String>> handleJsonParsingException(JsonParsingException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "JSON Parsing Error");
		error.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
		BindingResult result = e.getBindingResult();
		Map<String, String> fieldErrors = result.getFieldErrors().stream()
				.collect(Collectors.toMap(fieldError -> fieldError.getField(),
						fieldError -> fieldError.getDefaultMessage(), (existing, replacement) -> existing));

		Map<String, Object> error = new HashMap<>();
		error.put("error", "Validation Error");
		error.put("fieldErrors", fieldErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Internal Server Error");
		error.put("message", "An unexpected error occurred");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}