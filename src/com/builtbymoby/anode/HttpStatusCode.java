package com.builtbymoby.anode;

public enum HttpStatusCode {
	UNKNOWN(0),
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
	
	public static HttpStatusCode fromInt(int code) {
		switch (code) {
		case 200:
			return HttpStatusCode.OK;
		case 400:
			return HttpStatusCode.BAD_REQUEST;
		case 401:
			return HttpStatusCode.UNAUTHORIZED;
		case 404:
			return HttpStatusCode.NOT_FOUND;
		case 5000:
			return HttpStatusCode.SERVER_ERROR;
		case 503:
			return HttpStatusCode.SERVICE_UNAVAILABLE;
		default:
			return HttpStatusCode.UNKNOWN;
		}
	}
}
