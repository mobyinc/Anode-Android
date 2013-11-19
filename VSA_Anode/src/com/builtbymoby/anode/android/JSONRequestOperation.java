package com.builtbymoby.anode.android;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import android.util.Log;


/**
 * @author davidadams
 *
 */
public class JSONRequestOperation {

	public JSONRequestOperation(){
	}
	
	
	public JSONResponse JSONRequestOperationWithRequest(String urlRequest){
		
		JSONResponse jsonResponse = null;
		
		try{
			
			 SchemeRegistry schemeRegistry = new SchemeRegistry();
			 schemeRegistry.register(new Scheme("https", 
			            SSLSocketFactory.getSocketFactory(), 443));
			 
			 HttpParams params = new BasicHttpParams();
			 org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params, 20000); 
			 org.apache.http.params.HttpConnectionParams.setSoTimeout(params, 20000);
			 
			 AndroidHttpClient client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
			  
			 HttpUriRequest httpRequest = new HttpGet("http://ec2-54-201-85-141.us-west-2.compute.amazonaws.com/api/v1/product_categories");
			 HttpResponse response = client.execute(httpRequest);
			
			 StatusLine statusLine = response.getStatusLine();
	       	   
	       	 if (statusLine.getStatusCode() == 200) {
	       		
	       		StringBuilder builder = new StringBuilder(); 
	       		HttpEntity entity = response.getEntity();
       	        InputStream content = entity.getContent();
       	        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
       	        
       	        String line;
       	        while ((line = reader.readLine()) != null) {
       	        	
       	          builder.append(line);
       	        }
       	       
       	       client.close();
       	     
       	       //result = new JSONObject(builder.toString());
       	       
       	       jsonResponse = new JSONResponse(builder.toString());
    
		     }
	       	 
	       	 
			
		}catch(Throwable t){
			Log.e("JSONRequestOperation", "JSONRequestOperation.JSONRequestOperationWithRequest()", t);
		}
		
		return jsonResponse;
	}
	
	
	
}
