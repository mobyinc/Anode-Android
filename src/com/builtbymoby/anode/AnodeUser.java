package com.builtbymoby.anode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class AnodeUser extends AnodeObject implements Serializable {
	private static final long serialVersionUID = -856038997436386638L;
	private static final String CURRENT_USER_CACHE_KEY = "current_user_cach_key";
	
	private static AnodeUser currentUser = null;
	
	private Boolean authenticated;
	
	public AnodeUser() {
		super("user");
	}
	
	public AnodeUser(Long objectId) {
		super("user", objectId);
	}
	
	public AnodeUser(String username, String password) {
		super("user");
		this.setUsername(username);
		this.setPassword(password);
	}
	
	// TODO: implement cached user object
	public static AnodeUser getCurrentUser() {
		if (AnodeUser.currentUser == null) {
			AnodeUser.currentUser = (AnodeUser)AnodeCache.getInstance().getObject(CURRENT_USER_CACHE_KEY);
		}
		
		return AnodeUser.currentUser;
	}
	
	public static void setCurrentUser(AnodeUser user) {
		// TODO: why isn't cached version saving nested JSONObjects?
		AnodeUser.currentUser = user;
		Anode.setUserToken(user.getToken());
		AnodeCache.getInstance().putObject(CURRENT_USER_CACHE_KEY, user);
	}
	
	public static AnodeUser createFromJson(JSONObject json) {	
		String type = null;
		
		try {
			type = json.getString("__type");
		} catch (JSONException e) {
			throw new AnodeException(AnodeException.INVALID_JSON_OBJECT_TYPE, "__type parameter for object is missing or invalid", e);
		}
		
		if (TextUtils.isEmpty(type)) {
			throw new AnodeException(AnodeException.INVALID_JSON_OBJECT_TYPE, "__type parameter for object is missing or invalid");
		}
		
		AnodeUser user = new AnodeUser();
		
		applyJSON(json, user);
		
		return user;
	}
	
	public static void login(String username, String password, final LoginCallback callback) {
		List<NameValuePair> parameters = null;
		
		if (username != null && password != null) {
			parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("username", username));
			parameters.add(new BasicNameValuePair("password", password));
		}
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.POST, "user", null, "login", parameters, null);
		
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {
				if (response.isJSONObject()) {
					JSONObject node = response.getJSONObject();
					AnodeUser user = AnodeUser.createFromJson(node);
					user.authenticated = true;
					
					AnodeUser.setCurrentUser(user);
					
					callback.done(user);
				} else {
					throw new AnodeException(AnodeException.INVALID_JSON, "unexpected root node in login JSON response");
				}
			}

			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	public static void logout() {
		AnodeUser.currentUser = null;
		Anode.setUserToken(null);
		AnodeCache.getInstance().putObject(CURRENT_USER_CACHE_KEY, null);
	}
	
	public static void refreshLogin(final LoginCallback callback) {
		if (AnodeUser.getCurrentUser() == null) {
			callback.fail(new AnodeException(AnodeException.CURRENT_USER_NOT_INITIALIZED, "Cannot refresh login while logged out"));
		} else {
			// TODO: other login providers
			AnodeUser.login(null, null, callback);
		}
	}
	
	public static void registerDevice(String regId) {
		registerDevice(regId, null);
	}
	
	public static void registerDevice(String regId, final CompletionCallback callback) {
	    if (getCurrentUser() == null) {
	    	throw new AnodeException(AnodeException.CURRENT_USER_NOT_INITIALIZED, "Cannot register device token without current user");
	    }
	    
	    List<NameValuePair> parameters = new ArrayList<NameValuePair>();	
		parameters.add(new BasicNameValuePair("device_token", regId));
		parameters.add(new BasicNameValuePair("platform", "Android"));
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.POST, "user", null, "register_device_token", parameters, null);
		
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {				
				callback.done(null);				
			}
			
			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	public static void resetPassword(String username, final CompletionCallback callback) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();	
		parameters.add(new BasicNameValuePair("username", username));
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.POST, "user", null, "reset_password", parameters, null);
		
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {				
				callback.done(null);				
			}
			
			@Override
			public void fail(AnodeException e) {
				callback.fail(e);
			}
		});
	}
	
	/*
	 * Getters / Setters
	 */
	
	public static boolean isLoggedIn() {
		return getCurrentUser() != null;
	}

	public String getUsername() {
		return getString("username");
	}

	public void setUsername(String username) {
		setObject("username", username);
	}

	public String getPassword() {
		return getString("password");
	}

	public void setPassword(String password) {
		setObject("password", password);
	}

	public Boolean isAuthenticated() {
		return authenticated;
	}
	
	public String getToken() {
		return getString("__token");
	}
}
