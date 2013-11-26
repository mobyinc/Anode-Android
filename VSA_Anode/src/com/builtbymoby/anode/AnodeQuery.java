package com.builtbymoby.anode;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnodeQuery extends AnodeClient{
	private int skip = 0;
	private int limit = 100;
	private String orderBy = null;
	private OrderDirection orderDirection = OrderDirection.ASCENDING;
	
	// TODO: cache policy
	// TODO: relationship queries
	
	public enum OrderDirection {
		ASCENDING, DECENDING
	}
	
	public AnodeQuery(String type){
		super(type);
	}
	
	public void findAllObjects(final ObjectsResultCallback callback) {
		HttpUriRequest request = buildHttpRequest(HttpVerb.GET);
		
		fetchObjects(request, callback);
	}
	
	/*
	 * Private
	 */
	
	private void fetchObjects(HttpUriRequest request, final ObjectsResultCallback callback) {
		// TODO: caching
		
		fetchObjectFromNetwork(request, callback);
	}
	
	private void fetchObjectFromNetwork(HttpUriRequest request, final ObjectsResultCallback callback) {
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
}
