package com.builtbymoby.anode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.builtbymoby.anode.AnodePredicate.Operator;

import android.util.Log;

public class AnodeQuery extends AnodeClient{
	private Long skip = null;
	private Integer limit = null;
	private String orderBy = null;
	private OrderDirection orderDirection = OrderDirection.ASCENDING;
	private AnodeRelationship relationship;
	
	// TODO: cache policy
	// TODO: relationship queries
	
	public enum OrderDirection {
		ASCENDING("ASC"), DECENDING("DESC");
		
		private final String value;
		
		private OrderDirection(final String value) {
			this.value = value;
		}
		
		@Override
	    public String toString() {
	        return value;
	    }
	}
	
	public AnodeQuery(String type){
		super(type);
	}
	
	public AnodeQuery(String type, String belongsToType, String belongsToRelationshipName, Long belongsToObjectId) {
		super(type);
		
		relationship = new AnodeRelationship(belongsToType, belongsToRelationshipName, belongsToObjectId);
	}
	
	/*
	 * Parameter getter / setters
	 */

	public Long getSkip() {
		return skip;
	}

	public void setSkip(Long skip) {
		this.skip = skip;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}	
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public OrderDirection getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(OrderDirection orderDirection) {
		this.orderDirection = orderDirection;
	}

	/*
	 * Finders
	 */
	public void findAllObjects(final ObjectsResultCallback callback) {
		findObjects(null, null, null, callback);
	}
	
	public void findObjects(final ObjectsResultCallback callback) {
		findObjects(null, skip, limit, callback);
	}
	
	public void findObjects(String methodName, List<NameValuePair>parameters, final ObjectsResultCallback callback) {
		if (isRelationship()) {
			Log.w("AnodeQuery", "relationships are ignored when calling a specific method");
		}
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.GET, null, methodName, parameters);
		
		fetchObjects(request, callback);
	}
	
	public void findObjects(AnodePredicate predicate, final ObjectsResultCallback callback) {
		findObjects(predicate, skip, limit, callback);
	}
	
	public void findObjectById(final Long objectId, final ObjectResultCallback callback) {
		if (isRelationship()) {
			Log.w("AnodeQuery", "relationships are ignored when calling a specific method");
		}
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.GET, objectId);
		
		fetchObjects(request, new ObjectsResultCallback() {
			@Override
			public void done(List<AnodeObject> objects) {
				if (objects.size() > 0) {
					callback.done(objects.get(0));
				} else {
					callback.fail(new AnodeException(AnodeException.OBJECT_NOT_FOUND, "no object with id " + objectId));
				}
			}
			
			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	public void findObjects(List<Long> ids, final ObjectsResultCallback callback) {
		AnodePredicate predicate = new AnodePredicate("id", Operator.IN, ids);
		findObjects(predicate, callback);
	}
	
	/*
	 * Getters
	 */
	
	public Boolean isRelationship() {
		return relationship != null;
	}
	
	/*
	 * Protected
	 */
	
	protected void findObjects(AnodePredicate predicate, Long skip, Integer limit, ObjectsResultCallback callback) {
		HttpUriRequest request = null;
		
		if (predicate != null || limit != null || this.getOrderBy() != null || isRelationship()) {
			request = buildHttpRequest(HttpVerb.POST, "query");
			HttpPost post = (HttpPost)request;
			JSONObject json = jsonRequestRepresentation(predicate, skip, limit, this.getOrderBy(), this.getOrderDirection(), false);
			HttpEntity entity = getJsonHttpEntity(json);
			post.setEntity(entity);
		} else {
			request = buildHttpRequest(HttpVerb.GET);
		}
		
		fetchObjects(request, callback);
	}
	
	protected void fetchObjects(HttpUriRequest request, final ObjectsResultCallback callback) {
		// TODO: caching
		// always hit network for now
		
		fetchObjectsFromNetwork(request, callback);
	}
	
	protected void fetchValues(HttpUriRequest request, final CompletionCallback callback) {
		// TODO: implement value queries
	}
	
	protected void fetchObjectsFromNetwork(HttpUriRequest request, final ObjectsResultCallback callback) {
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {
				ArrayList<AnodeObject> objects = new ArrayList<AnodeObject>();
				
				if (response.isJSONArray()) {
					JSONArray jsonArray = response.getJSONArray();
					
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject node = jsonArray.optJSONObject(i);
						
						if (node != null) {
							AnodeObject object = AnodeObject.createFromJson(node);
							objects.add(object);
						} else {
							break;
						}
					}
				} else if (response.isJSONObject()) {
					JSONObject node = response.getJSONObject();
					AnodeObject object = AnodeObject.createFromJson(node);
					objects.add(object);
				} else {
					throw new AnodeException(AnodeException.INVALID_JSON, "unexpected root node in JSON response");
				}
				
				callback.done(objects);
			}

			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	protected JSONObject jsonRequestRepresentation(AnodePredicate predicate, Long skip, Integer limit, String orderBy, OrderDirection orderDirection, Boolean countOnly) {
		JSONObject object = new JSONObject();
		
		try {
			
			if (limit != null) {
				object.put("limit", limit);
			}
			
			if (skip != null) {
				object.put("skip", skip);
			}
			
			if (orderBy != null) {
				object.put("order_by", orderBy);
				object.put("order_direction", orderDirection.toString());
			}
			
			if (predicate != null) {
				object.put("predicate", predicate.toJson());
			}
			
			if (relationship != null) {
				object.put("relationship", relationship.toJson());
			}
			
			if (countOnly) {
				object.put("count_only", true);
			}
			
		} catch (JSONException e) {
			throw new AnodeException(AnodeException.JSON_ENCODING_ERROR, "query encoding error");
		}
		
		return object;
	}
}
