package com.builtbymoby.anode.android;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class AnodeQuery extends AnodeClient{

	
	public AnodeQuery(Context context){
		super(context);
	}
	
	
	public JSONResponse findObjectsWithMethod(String methodName){
		JSONResponse response = null;
		
		try{
			// NSMutableURLRequest* request = [self requestForVerb:@"GET" objectId:nil action:methodName parameters:parameters];
			//String request = this.requestForVerb("GET", -1, methodName);
			
			
			HttpUriRequest httpRequest = new HttpGet(this.baseURLString + "/product_categories");
			fetchObjectsWithRequest(httpRequest);
			
		}catch(Throwable t){
			Log.e("AnodeQuery", "AnodeQuery.findObjectsWithMethod()", t);
		}
	
		
		return response;
		
	}
	
	
	
}
