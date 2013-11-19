package com.builtbymoby.anode.android;


import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AnodeClient {

	
	private ResponseListener responseListener;
	
	protected Context mContext;
	protected String baseURLString = null;
	protected Handler callbackHandler;
	protected int callbackHandlerWhat = -1;
	
	public AnodeClient(Context context){
		
		this.mContext = context;
		
		//Retrieve the base url of the server from the Manifest: Activity META_DATA
		String packageName = mContext.getPackageName();
		
		try{
			
			//Get base url meta data from the Manifest file.
			ApplicationInfo appInfo = 
			mContext.getApplicationContext().getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appInfo.metaData;
			this.baseURLString = bundle.getString("base_url");
			
		}catch(NameNotFoundException nnfe){
			Log.e("Anode", "Base URL Metadata not found in the manifest.", nnfe);
		}
		
	}
	
	
	
	/**
	 * @param verb
	 * @param objectId
	 * @param action
	 * @return
	 */
	public String requestForVerb(String verb, long objectId, String action){
		
		String requestString = null;
		
		try{
			String path = this.pathForType(null, objectId, action);
			
		}catch(Throwable t){
			Log.e("AnodeClient", "AnodeClient.requestForVerb(", t);
		}
		
		return requestString;
	}
	
	
	/**
	 * @param type
	 * @param objectId
	 * @param action
	 * @return the path based on the type
	 */
	private String pathForType(String type, long objectId, String action){
		String path = null;
		
		//Formatting the path
		try{
			
			path = "/product_categories";
			
		}catch(Exception e){
			
		}
		return path;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	protected void fetchObjectsWithRequest(final HttpUriRequest request){
		
		try{
			
			System.out.println("######## fetchObjectsWithRequest called. " );
			
			AnodeHTTPClient anodeHTTPClient = AnodeHTTPClient.getInstance();
		
			anodeHTTPClient.get(this, request);
			
		}catch(Throwable t){
			Log.e("AnodeClient", "AnodeClient.fetObjectsWithRequest()", t);
		}	
	}
		
	
	public void setResponseListener(ResponseListener responseListener){
		this.responseListener = responseListener;
	}
	
	
	public ResponseListener getResponseListener(){
		return this.responseListener;
	}
	
	

	public static abstract class ResponseListener{
		
		public ResponseListener(){}
		
		public abstract void onResponse(JSONResponse response);
		
	}
	
	
}
