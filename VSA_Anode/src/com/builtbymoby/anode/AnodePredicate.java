package com.builtbymoby.anode;

import org.json.JSONException;
import org.json.JSONObject;

public class AnodePredicate {
	public Object left;
	public Object right;
	public Operator operator;
	
	public AnodePredicate(Object left, Operator operator, Object right) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}
	
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		try {
			object.put("left", left);
			object.put("right", right);
			object.put("operator", operator.toString());
		} catch (JSONException e) {
			throw new AnodeException(AnodeException.JSON_ENCODING_ERROR, "predicate encoding error");
		}
		
		return object;
	}
	
	public enum Operator {
		EQUAL("="),
		GREATER_THAN(">"),
		GREATER_THAN_OR_EQUAL(">="),
		LESS_THAN("<"),
		LESS_THAN_OR_EQUAL("<="),
		IN("in");
		
		private final String value;
		
		private Operator(final String value) {
			this.value = value;
		}
		
		@Override
	    public String toString() {
	        return value;
	    }
	}
}
