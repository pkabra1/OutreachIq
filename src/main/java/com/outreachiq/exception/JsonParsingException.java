package com.outreachiq.exception;

public class JsonParsingException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JsonParsingException(String message) {
		super(message);
	}

	public JsonParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}