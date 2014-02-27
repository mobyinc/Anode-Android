package com.builtbymoby.anode;

import org.json.JSONException;
import org.json.JSONObject;

public class AnodeRelationship {
	public String belongsToType;
	public String belongsToRelationshipName;
	public long belongsToObjectId;
	
	public AnodeRelationship(String belongsToType, String belongsToRelationshipName, long belongsToObjectId) {
		this.belongsToType = belongsToType;
		this.belongsToRelationshipName = belongsToRelationshipName;
		this.belongsToObjectId = belongsToObjectId;
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		try {
			object.put("type", belongsToType);
			object.put("name", belongsToRelationshipName);
			object.put("object_id", belongsToObjectId);
		} catch (JSONException e) {
			throw new AnodeException(AnodeException.JSON_ENCODING_ERROR, "relationship encoding error");
		}
		
		return object;
	}
}
