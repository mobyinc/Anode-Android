package com.builtbymoby.anode;

import android.util.Log;

public class AnodeException extends RuntimeException {
	private static final String TAG = "Anode";
	private static final long serialVersionUID = -4563946665411794592L;

	public static final int NOT_IMPLEMENTED = 1;
	public static final int NETWORK_ERROR = 2;
	public static final int INVALID_APP_INFO = 3;
	public static final int INVALID_BASE_URL = 4;
	public static final int INVALID_CLIENT_TOKEN = 5;
	public static final int INVALID_JSON = 6;
	public static final int INVALID_JSON_OBJECT_TYPE = 7;
	public static final int JSON_ENCODING_ERROR = 8;
	public static final int PARAMETER_ENCODING_ERROR = 9;
	public static final int SERVER_ERROR = 10;
	public static final int OBJECT_NOT_FOUND = 11;
	public static final int ILLEGAL_ATTRIBUTE = 12;
	public static final int CACHE_NOT_INITIALIZED = 13;
	public static final int CURRENT_USER_NOT_INITIALIZED = 14;
	public static final int INVALID_OBJECT_STATE = 15;
	
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
