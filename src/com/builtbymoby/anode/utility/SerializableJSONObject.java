package com.builtbymoby.anode.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SerializableJSONObject extends JSONObject implements Serializable {

	public SerializableJSONObject() {
		// TODO Auto-generated constructor stub
	}

	public SerializableJSONObject(Map copyFrom) {
		super(copyFrom);
		// TODO Auto-generated constructor stub
	}

	public SerializableJSONObject(JSONTokener readFrom) throws JSONException {
		super(readFrom);
		// TODO Auto-generated constructor stub
	}

	public SerializableJSONObject(String json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}

	public SerializableJSONObject(JSONObject copyFrom, String[] names)
			throws JSONException {
		super(copyFrom, names);
		// TODO Auto-generated constructor stub
	}
			
	// This is a bit messy
	// What happened to JSONObject.getNames() ?!
	public static JSONObject fromJSONObject(JSONObject copyFrom)
			throws JSONException {
		
		List<String> names = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Iterator<String> i = copyFrom.keys();
		while(i.hasNext()) {
			names.add(i.next());
		}
		
		String[] stringNames = new String[names.size()];
		stringNames = names.toArray(stringNames);
		
		return new SerializableJSONObject(copyFrom, stringNames);
	}
}
