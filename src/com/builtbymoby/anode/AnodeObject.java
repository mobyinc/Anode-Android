package com.builtbymoby.anode;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.builtbymoby.anode.utility.SerializableJSONObject;

import android.text.TextUtils;
import android.util.Log;

public class AnodeObject extends AnodeClient implements Serializable {
	private static final long serialVersionUID = 3788378882736332423L;
	
	private Boolean emptyObject = false;
	private Boolean dirty = false;
	private Boolean destroyOnSave = false;
	
	private HashMap<String, Object> data = new HashMap<String, Object>();
	
	// TODO: file storage
	
	public AnodeObject(String type){
		super(type);
	}
	
	public AnodeObject(String type, Long objectId) {
		super(type);
		
		data.put("id", objectId);		
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
		data.put("_destroy", destroyOnSave);		
	}
	
	public void setObject(String key, Object object) {
		if (key.contains("__")) {
			throw new AnodeException(AnodeException.ILLEGAL_ATTRIBUTE, "cannot access protected attribute " + key);
		}
		
		Object existingValue = data.get(key);
		
		if (existingValue == null || !existingValue.equals(object)) {
			dirty = true;
			data.put(key, object);
		}
	}
	
	public void removeObject(String key) {
		data.put(key, null);
	}
	
	public Object getObject(String key) {		
		return data.get(key);
	}
	
	public String getString(String key) {
		return (String)data.get(key);
	}
	
	public Boolean getBoolean(String key) {
		return (Boolean)data.get(key);
	}
	
	public Integer getInteger(String key) {
		return (Integer)data.get(key);
	}
	
	public Double getDouble(String key) {
		   return (Double)(data.get(key));
	}
	
	public Long getLong(String key) {
		
		try{
		    return (Long)data.get(key);
		}catch(ClassCastException cce){
			return new Long((Integer)data.get(key));
		}
	}
	
	public Date getDate(String key) {
		return (Date)data.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<AnodeObject> getList(String key) {
		if (data.containsKey(key) && data.get(key) instanceof List<?>) {
			return (List<AnodeObject>)data.get(key);
		} else {
			return new ArrayList<AnodeObject>();
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
		object.data = new HashMap<String, Object>();
		object.dirty = false;
		object.emptyObject = false;
		
		Iterator<?> keys = json.keys();
		
		while (keys.hasNext()) {
			String key = (String)keys.next();			
			Object value = json.opt(key);			
			
			// handle special types which need to be converted
			// other types pass through untouched
			if (value instanceof String) {
				String stringValue = value.toString();
				
				if (isDateString(stringValue)) {
					try {
						value = dateFormat.parse(stringValue);
					} catch (ParseException e) {						
					}
				}
				
				// TODO: null string representation check?
			} else if (value instanceof JSONArray) {
				JSONArray arrayValue = (JSONArray)value;
				
				List<AnodeObject> list = new ArrayList<AnodeObject>();
				
				for (int i = 0; i < arrayValue.length(); i++) {
					JSONObject jsonObject = arrayValue.optJSONObject(i);
					if (jsonObject != null) {
						AnodeObject listObject = AnodeObject.createFromJson(jsonObject);
						list.add(listObject);
					}
				}
				
				value = list;
			} else if (value instanceof JSONObject) {
				JSONObject jsonValue = (JSONObject)value;
				
				String type = jsonValue.optString("__type");
				
				if (!TextUtils.isEmpty(type)) {
					value = AnodeObject.createFromJson(jsonValue);
				} else {
					try {
						value = SerializableJSONObject.fromJSONObject(jsonValue);
					} catch (JSONException e) {
						Log.e("AnodeObject", e.getLocalizedMessage());
						value = null;
					}
				}
			}
			
			if (value != null) {
				object.data.put(key, value);
			}
		}
	}
	
	private JSONObject toJson() {
		// TODO: Implement
		return new JSONObject();
	}
	
	private static boolean isDateString(String value) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{4}";
		return value.matches(pattern);
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
			
			applyJSON(json, this);
		} else {
			throw new AnodeException(AnodeException.INVALID_JSON, "missing object root node in server response");
		}
	}
}
