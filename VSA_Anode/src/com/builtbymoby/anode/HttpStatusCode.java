package com.builtbymoby.anode;

public enum HttpStatusCode {
	OK(200), 
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	SERVER_ERROR(500),
	SERVICE_UNAVAILABLE(503);
	
	private int value;
	
	private HttpStatusCode(int value) {
		this.value = value;
	}
}
