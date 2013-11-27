package com.builtbymoby.anode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class AnodeObject extends AnodeClient {

	private Boolean emptyObject = false;
	private Boolean dirty = false;
	private Boolean destroyOnSave = false;
	
	private JSONObject data;
	
	// TODO: file storage
	
	public AnodeObject(String type){
		super(type);
	}
	
	public AnodeObject(String type, long objectId) {
		super(type);
		
		setObjectId(objectId);
		emptyObject = true;
	}
	
	public static AnodeObject createFromJson(JSONObject json) {	
		String type = null;
		
		try {
			type = json.getString("__type");
		} catch (JSONException e) {
			throw new AnodeException(AnodeException.INVALID_JSON_OBJECT_TYPE, "__type parameter for object is missing or invalid", e);
		}
		
		if (TextUtils.isEmpty(type)) {
			throw new AnodeException(AnodeException.INVALID_JSON_OBJECT_TYPE, "__type parameter for object is missing or invalid");
		}
		
		AnodeObject object = new AnodeObject(type);
		
		applyJSON(json, object);
		
		return object;
	}
	
	// TODO: constructor for encoded cached copy
	
	public Long getObjectId() {
		return getLong("id");
	}
	
	public void setObjectId(Long objectId) {
		setObject(objectId, "id");
	}
	
	public Date getCreatedAt() {
		return (Date)getObject("created_at");
	}
	
	public Date getUpdatedAt() {
		return (Date)getObject("updated_at");
	}
	
	public Boolean isNew() {
		return getObjectId() == null;
	}
	
	public Boolean isEmpty() {
		return emptyObject;
	}
	
	public Boolean isDirty() {
		return dirty;
	}
	
	public void setDestroyOnSave(Boolean shouldDestroy) {
		destroyOnSave = shouldDestroy;
		
		try {
			data.put("_destroy", destroyOnSave);
		} catch (JSONException e) {
		}		
	}
	
	public void setObject(Object object, String key) {
		if (key.contains("__")) {
			throw new AnodeException(AnodeException.ILLEGAL_ATTRIBUTE, "cannot access protected attribute " + key);
		}
		
		Object existingValue = data.opt(key);
		
		if (object == null) {
			removeObject(key);
		} else if (existingValue == null || !existingValue.equals(object)) {
			dirty = true;
			try {
				data.put(key, object);
			} catch (JSONException e) {
			}			
		}
	}
	
	public void removeObject(String key) {
		// TODO: what is the correct string representation of null?
	}
	
	public Object getObject(String key) {		
		return data.opt(key);
	}
	
	public String getString(String key) {
		return data.optString(key);
	}
	
	public Boolean getBoolean(String key) {
		return data.optBoolean(key, false);
	}
	
	public Integer getInteger(String key) {
		try {
			return data.getInt(key);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public Long getLong(String key) {
		try {
			return data.getLong(key);
		} catch (JSONException e) {
			return null;
		}
	}
	
	// TODO: other getter types
	
	public AnodeFile getFile(String key, String version) {
		JSONObject node = (JSONObject)getObject(key);
		String url = node.optString(version, "");
		return new AnodeFile(url);
	}
	
	// TODO: file support
	
	public void save() {
		// TODO: implement save object
	}
	
	public void reload() {
		// TODO: implement reload object
	}
	
	public void destroy() {
		// TODO: implement destroy object
	}
	
	public void touch() {
		dirty = true;
	}
	
	public AnodeObject clone() {
		// TODO: implement clone object
		return null;
	}
	
	public void callMethod(String methodName, List<NameValuePair>parameters, CompletionCallback callback) {
		// TODO: implement method calling
	}
	
	public AnodeQuery getQuery(String relationshipName, String type) {
		return new AnodeQuery(type, this.type, relationshipName, this.getObjectId());
	}
	
	/*
	 * Private
	 */
	
	// TODO: perform request (for CRUD operations)
	
	private static void applyJSON(JSONObject json, AnodeObject object) {
		object.data = json;
		object.dirty = false;
		object.emptyObject = false;
		
		// Handle special data types
	}
	
	private void performRequest(HttpVerb verb, String httpBody, CompletionCallback callback) {
		// TODO: implement object operations
	}
	
	private void applyJSONResponse(JsonResponse response) {
		if (response != null && response.isJSONObject()) {
			JSONObject json = response.getJSONObject();
			
			try {
				long objectId = json.getLong("id");
				if (objectId <= 0) {
					throw new Exception("object id is not valid");
				}
			} catch (Exception e) {
				throw new AnodeException(AnodeException.INVALID_JSON, "invalid object id on root node", e);
			}
			
			// TODO: apply
		} else {
			throw new AnodeException(AnodeException.INVALID_JSON, "missing object root node in server response");
		}
	}
}
