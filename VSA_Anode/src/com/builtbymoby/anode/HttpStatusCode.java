package com.builtbymoby.anode;

public enum HttpStatusCode {
	OK(200), 
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	NOT_FOUND(404),
	SERVER_ERROR(500),
	SERVICE_UNAVAILABLE(503);
	
	private final int value;
	
	private HttpStatusCode(final int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
