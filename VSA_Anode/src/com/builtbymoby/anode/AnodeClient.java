package com.builtbymoby.anode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.builtbymoby.anode.utility.inflector.English;

import android.annotation.SuppressLint;
import android.net.Uri;

public class AnodeClient {
	protected String type = "";
	
	@SuppressLint("DefaultLocale")
	public AnodeClient(String type) {
		this.type = type.toLowerCase();		
	}
	
	/**
	 * @return the type
	 */
	protected String getType() {
		return type;
	}	
	
	/**
	 * @param type
	 * @param objectId
	 * @param action
	 * @return the path based on the type
	 */
	private static String getPath(String type, long objectId, String action) {
		String typeSegment = English.plural(type);
		String path = null;
		
		if (action != null && objectId > 0) {
			path = String.format("%s/%s/%s", typeSegment, objectId, action);
		} else if (objectId > 0) {
			path = String.format("%s/%s", typeSegment, objectId);
		} else if (action != null) {
			path = String.format("%s/%s", typeSegment, action);
		} else {
			path = String.format("%s", typeSegment);
		}
		
		return path;
	}
	
	/**
	 * @param verb
	 * @param type
	 * @param objectId
	 * @param action
	 * @return
	 */
	public static HttpUriRequest buildHttpRequest(HttpVerb verb, String type, long objectId, String action, List<NameValuePair> parameters) {		
		String path = getPath(type, objectId, action);
		String token = Anode.getToken();
		Uri.Builder builder = getUriBuilder();
		HttpUriRequest request = null;
		
		builder.appendEncodedPath(path);		
		
		switch (verb) {
		case POST:
			
			break;

		default: // default is GET	
			for (NameValuePair nvp : parameters) {
				builder.appendQueryParameter(nvp.getName(), nvp.getValue());
			}
			
			request = new HttpGet(builder.build().toString());
			break;
		}
						
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Authorization", "Token token=" + token);
		
		return request;
	}

	public HttpUriRequest buildHttpRequest(HttpVerb verb) {
		return buildHttpRequest(verb, 0, null, new ArrayList<NameValuePair>());
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, long objectId) {
		return buildHttpRequest(verb, objectId, null, new ArrayList<NameValuePair>());
	}	
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, String action) {
		return buildHttpRequest(verb, 0, action, new ArrayList<NameValuePair>());
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, long objectId, String action, List<NameValuePair> parameters) {
		return AnodeClient.buildHttpRequest(verb, this.type, objectId, action, parameters);
	}
	
	/*
	 * Private Methods
	 */
	
	private static Uri.Builder getUriBuilder() {
		URI uri = null;
		
		try {
			uri = new URI(Anode.baseUrl);
		} catch (URISyntaxException e) {			
			throw new AnodeException(AnodeException.INVALID_BASE_URL, "base url is not a valid url", e);
		}
		
		Uri.Builder builder = new Uri.Builder();		
		builder.scheme(uri.getScheme()).authority(uri.getAuthority()).path(uri.getPath());
		
		return builder;
	}
}
