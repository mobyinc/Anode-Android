package com.builtbymoby.anode;

import android.util.Log;

public class AnodeException extends RuntimeException {
	private static final String TAG = "Anode";
	private static final long serialVersionUID = -4563946665411794592L;

	public static final int NOT_IMPLEMENTED = 1;
	
	public static final int NETWORK_ERROR = 100;
	
	public static final int INVALID_APP_INFO = 200;
	public static final int INVALID_BASE_URL = 201;
	public static final int INVALID_CLIENT_TOKEN = 202;
	
	public static final int INVALID_JSON = 400;
	public static final int INVALID_JSON_OBJECT_TYPE = 401;
	
	public static final int SERVER_ERROR = 500;
	
	public static final int ILLEGAL_ATTRIBUTE = 600;
	
	private int code = 0;
	private int httpStatusCode = 0;
	
	public AnodeException(int code, String message) {
		super(message);
		this.code = code;
		Log.e(TAG, message, this);
	}
	
	public AnodeException(int code, int httpStatusCode, String message) {
		super(message);
		this.code = code;
		this.httpStatusCode = httpStatusCode;
		Log.e(TAG, message, this);
	}
	
	public AnodeException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		Log.e(TAG, message, this);
	}
	
	public AnodeException(int code, int httpStatusCode, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.httpStatusCode = httpStatusCode;
		Log.e(TAG, message, this);
	}
	
	public int getCode() {
		return code;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
