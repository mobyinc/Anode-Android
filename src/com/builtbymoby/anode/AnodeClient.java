package com.builtbymoby.anode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.entity.mime.content.ContentBody;
import ch.boye.httpclientandroidlib.protocol.HTTP;

import com.builtbymoby.anode.utility.inflector.English;

import android.annotation.SuppressLint;
import android.net.Uri;

@SuppressLint("SimpleDateFormat")
public class AnodeClient implements Serializable {

	private static final long serialVersionUID = -4825268292630127746L;

	static protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ");
	
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
	private static String getPath(String type, Long objectId, String action) {
		String typeSegment = English.plural(type);
		String path = null;
		
		if (action != null && objectId != null) {
			path = String.format("%s/%s/%s", typeSegment, objectId, action);
		} else if (objectId != null) {
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
	public static HttpUriRequest buildHttpRequest(HttpVerb verb, String type, Long objectId, String action, List<NameValuePair> parameters, String httpBody) {		
		String path = getPath(type, objectId, action);
		String token = Anode.getToken();
		String contentType = "application/json";
		Uri.Builder builder = getUriBuilder();
		HttpUriRequest request = null;
		
		builder.appendEncodedPath(path);		
		
		switch (verb) {
		case POST:
		case PUT:
			
			if (verb == HttpVerb.POST) {
				request = new HttpPost(buildURI(builder));			
			} else {
				request = new HttpPut(buildURI(builder));
			}
			
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase)request;
			 
			if (parameters != null && parameters.size() > 0) {				
				contentType = "application/x-www-form-urlencoded";				
				
				try {
					entityRequest.setEntity(new UrlEncodedFormEntity(parameters));
				} catch (UnsupportedEncodingException e) {
					throw new AnodeException(AnodeException.PARAMETER_ENCODING_ERROR, "post parameters encoding error", e);
				}
			} else if (httpBody != null && httpBody.length() > 0) {
				try {
					entityRequest.setEntity(new StringEntity(httpBody));
				} catch (UnsupportedEncodingException e) {
					throw new AnodeException(AnodeException.PARAMETER_ENCODING_ERROR, "post body encoding error", e);
				}
			}
			
			break;
		case DELETE:			
			request = new HttpDelete(buildURI(builder));
			
			break;
		default: // default is GET	
			if (parameters != null) {
				for (NameValuePair nvp : parameters) {
					builder.appendQueryParameter(nvp.getName(), nvp.getValue());
				}
			}
			
			request = new HttpGet(buildURI(builder));
			break;
		}
						
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-Type", contentType);		
		request.setHeader("Authorization", "Token token=" + token);
		
		return request;
	}
	
	public static HttpUriRequest buildHttpRequest(HttpVerb verb, String type, Long objectId, String action, List<NameValuePair> parameters, String httpBody, Map<String, AnodeFile>files) {
		String path = getPath(type, objectId, action);
		String token = Anode.getToken();		
		Uri.Builder uriBuilder = getUriBuilder();		
		uriBuilder.appendEncodedPath(path);		
		
		HttpEntityEnclosingRequestBase request = null;
		
		if (verb == HttpVerb.POST) {
			request = new HttpPost(buildURI(uriBuilder));
		} else if (verb == HttpVerb.PUT) {
			request = new HttpPut(buildURI(uriBuilder));
		} else {
			throw new AnodeException(AnodeException.INVALID_HTTP_VERB, "Multipart requests must use POST or PUT verb");
		}	
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
		builder.setMode(HttpMultipartMode.STRICT);

		builder.addTextBody("DATA", httpBody, ContentType.APPLICATION_JSON);
		
		// TODO: parameters
		if (parameters != null) {
			throw new AnodeException(AnodeException.NOT_IMPLEMENTED, "multipart parameters are not supported yet");
		}
		
		for (String name : files.keySet()) {
			AnodeFile file = files.get(name);
			String formFieldName = type + "[" + name + "]";
			builder.addPart(formFieldName, file);
//			builder.addBinaryBody(file.getFileName(), file.getData());			
		}
		 
		final HttpEntity entity = builder.build();		
		HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase)request;
		entityRequest.setEntity(entity);
		    
		request.setHeader("Accept", "application/json");
//		request.setHeader("Content-Type", contentType);		
		request.setHeader("Authorization", "Token token=" + token);
		
		return request;
	}

	public HttpUriRequest buildHttpRequest(HttpVerb verb) {
		return buildHttpRequest(verb, null, null, new ArrayList<NameValuePair>());
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, Long objectId) {
		return buildHttpRequest(verb, objectId, null, new ArrayList<NameValuePair>());
	}	
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, String action) {
		return buildHttpRequest(verb, null, action, new ArrayList<NameValuePair>());
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, Long objectId, String action, List<NameValuePair> parameters) {
		return AnodeClient.buildHttpRequest(verb, this.type, objectId, action, parameters, null);
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, Long objectId, String action, String httpBody) {
		return AnodeClient.buildHttpRequest(verb, this.type, objectId, action, null, httpBody);
	}
	
	public HttpUriRequest buildHttpRequest(HttpVerb verb, Long objectId, String action, List<NameValuePair> parameters, String httpBody, Map<String, AnodeFile>files) {
		return AnodeClient.buildHttpRequest(verb, this.type, objectId, action, parameters, httpBody, files);
	}
	
	/*
	 * Protected
	 */
	
	protected static Uri.Builder getUriBuilder() {
		URI uri = null;
		
		try {
			uri = new URI(Anode.getBaseUrl());
		} catch (URISyntaxException e) {			
			throw new AnodeException(AnodeException.INVALID_BASE_URL, "base url is not a valid url", e);
		}
		
		Uri.Builder builder = new Uri.Builder();		
		builder.scheme(uri.getScheme()).authority(uri.getAuthority()).path(uri.getPath());
		
		return builder;
	}
	
	/**
	 * Converts Android URI to Java URI
	 * @param builder
	 * @return
	 */
	protected static URI buildURI(Uri.Builder builder) {
		Uri uri = builder.build();
		try {
			return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException ignored) {
			return null;
		}
	}
	
	protected static HttpEntity getJsonHttpEntity(Object json) {
		try {
			StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
			entity.setContentType("application/json");
			return entity;
		} catch (UnsupportedEncodingException e) {
			throw new AnodeException(AnodeException.JSON_ENCODING_ERROR, "JSON entity encoding error");
		}
	}
}
