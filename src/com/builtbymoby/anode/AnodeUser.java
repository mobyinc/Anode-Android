package com.builtbymoby.anode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class AnodeUser extends AnodeObject implements Serializable {
	private static final long serialVersionUID = -856038997436386638L;
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
	}
	
	public static AnodeUser currentUser() {
		if (AnodeUser.currentUser == null) {
			AnodeUser.currentUser = null;
		}
		
		return AnodeUser.currentUser;
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
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		
		if (username != null && password != null) {
			parameters.add(new BasicNameValuePair("username", username));
			parameters.add(new BasicNameValuePair("password", password));
		}
		
		HttpUriRequest request = buildHttpRequest(HttpVerb.POST, "user", null, "login", parameters);
		
		AnodeHttpClient.getInstance().perform(request, new JsonResponseCallback() {
			@Override
			public void done(JsonResponse response) {
				if (response.isJSONObject()) {
					JSONObject node = response.getJSONObject();
					AnodeUser user = AnodeUser.createFromJson(node);
					user.authenticated = true;
					
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
	
	public static void refreshLogin(final LoginCallback callback) {
		
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
}
