package com.builtbymoby.anode;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

public class Anode {
	private static Context context;
	private static String baseUrl;
	private static String userToken;
	private static String clientToken;
	
	public static void initialize(Context context) throws AnodeException {		
		ApplicationInfo appInfo = null;
		
		try {
			appInfo = context.getApplicationContext().getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			throw new AnodeException(AnodeException.INVALID_APP_INFO, "missing name key in app info", e);
		}
		
		Bundle bundle = appInfo.metaData;
		String baseUrl = bundle.getString("base_url");
		String clientToken = bundle.getString("client_token");
		
		if (TextUtils.isEmpty(baseUrl)) {
			throw new AnodeException(AnodeException.INVALID_BASE_URL, "missing base url in app info");
		}
		
		if (TextUtils.isEmpty(clientToken)) {
			throw new AnodeException(AnodeException.INVALID_CLIENT_TOKEN, "missing client token in app info");
		}
		
		Anode.context = context;
		Anode.baseUrl = baseUrl;
		Anode.clientToken = clientToken;
		
		AnodeCache.initialize(context, "ANODE_CACHE", 1024 * 1024 * 5); // 5 MB Cache

		// check for existing user session
		if (AnodeUser.isLoggedIn()) {
			Anode.userToken = AnodeUser.getCurrentUser().getToken();
		}
	}
	
	public static String getBaseUrl() {
		return baseUrl;
	}
	
	public static String getToken() {
		return userToken != null ? userToken : clientToken;
	}
	
	public static void setUserToken(String userToken) {
		Anode.userToken = userToken;		
	}
	
	public static void setCacheVersion(int version) {
		// TODO: implement cache versions
		throw new UnsupportedOperationException();
	}
}
