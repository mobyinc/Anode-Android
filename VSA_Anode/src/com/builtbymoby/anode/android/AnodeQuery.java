package com.builtbymoby.anode.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

public class AnodeQuery extends AnodeClient{

	
	private int skip = -1;
	private int limit = -1;
	private String orderBy = null;
	
	private String type = "";
	
	public AnodeQuery(Context context){
		super(context);
	}
	
	
	/**
	 * Analogous to the ios call to queryWithType
	 * @param type
	 */
	public AnodeQuery(Context context, String type){
		super(context);
		
		this.type = type.toLowerCase(); //Force lower case
		this.skip = 0;
		this.limit = 100;	
	}
	
	
	public JSONResponse findObjectsWithMethod(final String methodName){
		JSONResponse response = null;
		
		try{
			//NSMutableURLRequest* request = [self requestForVerb:@"GET" objectId:nil action:methodName parameters:parameters];
			//String request = this.requestForVerb("GET", -1, methodName);
			
			HttpUriRequest httpRequest;
			if(methodName.equalsIgnoreCase("GET")){
			    httpRequest = new HttpGet(this.baseURLString + type);
			}else if(methodName.equalsIgnoreCase("POST")){
				httpRequest = new HttpPost(this.baseURLString + type);
				
				//List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				//pairs.add(new BasicNameValuePair("", ""));
				//pairs.add(new BasicNameValuePair("", ""));
				//((HttpPost)httpRequest).setEntity(new UrlEncodedFormEntity(pairs));
				
				
			}else{
				return response;
			}
			
			fetchObjectsWithRequest(httpRequest);
			
		}catch(Throwable t){
			Log.e("AnodeQuery", "AnodeQuery.findObjectsWithMethod()", t);
		}
	
		
		return response;
		
	}
	
	
	
	
	/**
	 * Set the limit
	 * @param limit
	 */
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	
	/**
	 * set the orderBy String
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy){
		this.orderBy = orderBy;
	}
	
	
}
