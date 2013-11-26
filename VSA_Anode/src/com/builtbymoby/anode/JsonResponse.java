package com.builtbymoby.anode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Encapsulates a JSON response.
 */
public class JsonResponse {
	private JSONTokener tokener;
	private String jsonString;
	private boolean isArray;
	private boolean isValid;

	public JsonResponse(String jsonString) {
		this.jsonString = jsonString;
		tokener = new JSONTokener(jsonString);		
		isValid = true;
		
		try {
			isArray = (tokener.nextValue() instanceof JSONArray);
		} catch (JSONException e) {
			isValid = false;
		}		
	}
	
	public boolean isJSONObject() {
        return !isArray;
	}	
	
	public boolean isJSONArray() {
		return isArray;	
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public JSONObject getJSONObject() {
		if (isJSONObject()) {
	        try {
				return new JSONObject(jsonString);
			} catch (JSONException e) {
				return null;
			}
		}
		
	    return null;
	}
	
	public JSONArray getJSONArray() {
		if (isJSONArray()) {
			try {
				return new JSONArray(jsonString);
			} catch (JSONException e) {
				return null;
			}
		}

		return null;
	}
}
