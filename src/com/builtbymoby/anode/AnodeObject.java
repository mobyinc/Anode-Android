package com.builtbymoby.anode;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.builtbymoby.anode.utility.SerializableJSONObject;

import android.annotation.SuppressLint;
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
	
	public boolean getPrimitiveBoolean(String key) {
		Boolean value = (Boolean)data.get(key);
		return value != null && value;
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
		save(null);
	}
	
	public void save(CompletionCallback callback) {
		if (this.destroyOnSave) {
			destroy(callback);
			return;
		}
		
		if (!dirty) {
			if (callback != null) callback.done(this);
			return;
		} else if (this.emptyObject) {
			throw new AnodeException(AnodeException.INVALID_OBJECT_STATE, "Cannot save an empty object. Load by id or reload first");
		}	
		
		HttpVerb httpVerb = getObjectId() == null ? HttpVerb.POST : HttpVerb.PUT;
		JSONObject json = jsonRequestRepresentation();
		String jsonString = json.toString();
		
		performRequest(httpVerb, jsonString, callback);
	}
	
	public void reload() {
		// TODO: implement reload object
	}
	
	public void destroy() {
		// TODO: implement destroy object
	}
	
	public void destroy(CompletionCallback callback) {
		
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
	
	public void PrintDebug() {
		for (String key : this.data.keySet()) {
			Object value = this.data.get(key);
			Log.w("AnodeObject", "JSON key: " + key + " value: " + value + " type: " + value.getClass().getName());
		}
	}
	
	/*
	 * Private
	 */
	
	// TODO: perform request (for CRUD operations)
	
	protected static void applyJSON(JSONObject json, AnodeObject object) {
		object.data = new HashMap<String, Object>();
		object.dirty = false;
		object.emptyObject = false;
		
		Iterator<String> keys = json.keys();
		
		while (keys.hasNext()) {
			String key = keys.next();	
			Object value = null;
			
			if (!json.isNull(key)) {
				value = json.opt(key);
			}
			
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
		
	protected JSONObject jsonRequestRepresentation() {
		JSONObject wrapperObject = new JSONObject();
		JSONObject object = new JSONObject();
		
		try { wrapperObject.put(this.type, object); } catch (JSONException ignored) {}
		
		for (String key : data.keySet()) {
			Object value = data.get(key);
			
			if (value instanceof Date){
				value = dateFormat.format(value);
			} else if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<AnodeObject> list = (List<AnodeObject>)value;
				JSONObject objectMap = new JSONObject();
				
				for (int i = 0; i < list.size(); i++) {
					AnodeObject listObject = list.get(i);
					JSONObject listObjectJson = listObject.jsonRequestRepresentation();
					String name = listObject.isNew() ? String.format("%d", i+100000000) : listObject.getObjectId().toString();
					try { objectMap.putOpt(name, listObjectJson); } catch (JSONException e) { /* Oh well */ }
				}
				
				key = key + "_attributes";
				value = objectMap;
			} else if (value instanceof AnodeObject) {
				// only supports setting the relationship to an existing (non-new) object
				AnodeObject subObject = (AnodeObject)value;
				
				if (!subObject.isNew()) {
					key = key + "_id";
					value = subObject.getObjectId();					
				} else {
					key = null;
					value = null;
				}
			}
			
			if (key != null && value != null) {
				try { object.putOpt(key, value); } catch (JSONException ignored) {}
			}
		}
		
		return wrapperObject;
	}
	
	protected static boolean isDateString(String value) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{4}";
		return value.matches(pattern);
	}
	
	protected void performRequest(final HttpVerb verb, final String httpBody, final CompletionCallback callback) {
		HttpUriRequest request = buildHttpRequest(verb, getObjectId(), null, httpBody);
		
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {
				if (verb != HttpVerb.DELETE) {
					applyJSONResponse(response);
				}
				
				// TODO: issue with returning this and it ending up as a $1 (nested) class and not castable
				callback.done(this);
			}

			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	protected void applyJSONResponse(JsonResponse response) {
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
