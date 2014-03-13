package com.builtbymoby.anode;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Anode {
	private static Context context;
	private static String baseUrl;
	private static String userToken;
	private static String clientToken;
    private static String appId;
	private static AnodeErrorHandler errorHandler;
	
	public static void initialize(Context context) throws AnodeException {
		Anode.initialize(context, null);
	}
	
	public static void initialize(Context context, AnodeErrorHandler errorHandler) throws AnodeException {		
		ApplicationInfo appInfo = null;
		
		try {
			appInfo = context.getApplicationContext().getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			throw new AnodeException(AnodeException.INVALID_APP_INFO, "missing name key in app info", e);
		}
		
		Bundle bundle = appInfo.metaData;
		String baseUrl = bundle.getString("fluke_api_base_url");
		String clientToken = bundle.getString("fluke_api_client_token");
        String appId = bundle.getString("fluke_api_app_id");
        if (appId == null)
            appId = String.format("%d", bundle.getInt("fluke_api_app_id", 1));

		if (TextUtils.isEmpty(baseUrl)) {
			throw new AnodeException(AnodeException.INVALID_BASE_URL, "missing base url in app info");
		}
		
		if (TextUtils.isEmpty(clientToken)) {
			throw new AnodeException(AnodeException.INVALID_CLIENT_TOKEN, "missing client token in app info");
		}

		Anode.context = context;
		Anode.baseUrl = baseUrl;
        Anode.appId = appId;
		Anode.clientToken = clientToken;
		Anode.errorHandler = errorHandler;
		
		AnodeCache.initialize(Anode.context, "ANODE_CACHE", 1024 * 1024 * 5); // 5 MB Cache

		// check for existing user session
		if (AnodeUser.isLoggedIn()) {
			Anode.userToken = AnodeUser.getCurrentUser().getToken();
		}
	}
	
	public static String getBaseUrl() {
		return Anode.baseUrl;
	}
	
	public static String getToken() {
		return Anode.userToken != null ? Anode.userToken : Anode.clientToken;
	}

    public static String getAppId() {
        return Anode.appId;
    }

	public static AnodeErrorHandler getErrorHandler() {
		return Anode.errorHandler;
	}
	
	public static void setUserToken(String userToken) {
		Anode.userToken = userToken;		
	}
	
	public static void setCacheVersion(int version) {
		// TODO: implement cache versions
		throw new UnsupportedOperationException();
	}
}
