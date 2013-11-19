package com.builtbymoby.anode.android;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Log;


/**
 * HTTP Client singleton
 */
public class AnodeHTTPClient {

	private static AnodeHTTPClient anodeHTTPClient = null;
	
	
	/**
	 * 
	 * @return the singleton instance of AnodeHTTPClient
	 */
	public static AnodeHTTPClient getInstance(){
		
		if(anodeHTTPClient == null){
			anodeHTTPClient = new AnodeHTTPClient();
			
		}
		
		return anodeHTTPClient;
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public void get(final AnodeClient anodeClient, final HttpUriRequest httpRequest) throws IOException{
		
		AnodeHTTPClientThread httpThread = new AnodeHTTPClientThread(anodeClient, httpRequest); 
		httpThread.start();

	}
	
	
	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	//public void getJSON(final HttpUriRequest httpRequest, final Handler callbackHandler, final int callbackHandlerWhat) throws IOException{
		
		//return new JSONResponse(this.get(httpRequest, callbackHandler, callbackHandlerWhat));
		
	//}
	
	
	/**
	 * Thread class
	 */
	private class AnodeHTTPClientThread extends Thread{
		
		private HttpUriRequest httpRequest;
		private AnodeClient anodeClient;
		
		public AnodeHTTPClientThread(AnodeClient anodeClient, final HttpUriRequest httpRequest){
			this.httpRequest = httpRequest;
			this.anodeClient = anodeClient;
		}
		
		public void run(){
			
			
			  SchemeRegistry schemeRegistry = new SchemeRegistry();
			  schemeRegistry.register(new Scheme("https", 
			            SSLSocketFactory.getSocketFactory(), 443));
			
			
			  HttpParams params = new BasicHttpParams();
			  org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params, 20000); 
			  org.apache.http.params.HttpConnectionParams.setSoTimeout(params, 20000);
			  //AndroidHttpClient client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
			  HttpClient client = new DefaultHttpClient();
			  InputStream in = null;
			 
			  try {
				  
				  httpRequest.setHeader("Accept", "*/*");
				  httpRequest.setHeader("Accept-Encoding", "gzip, deflate");
				  httpRequest.setHeader("Authorization", "Token token=da40da5cf0dde5f3acafe736b88e46b8");
				  httpRequest.setHeader("Accept-Language", "en-us");
				
				  HttpResponse httpResponse = client.execute(httpRequest);
				  		  
				  HttpEntity entity = httpResponse.getEntity();
				  //System.out.println("STATUS: " + httpResponse.getStatusLine());
			      
				  in = AndroidHttpClient.getUngzippedContent(entity);

				  //StringBuffer builder = new StringBuffer();  
				  /**
			      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			      StringBuilder builder = new StringBuilder();       
		   	      String line;
		   	        
		   	      while ((line = reader.readLine()) != null) {
		   	          builder.append(line);
		   	      }  
			       */
			  
				  byte[] buffer = new byte[1024];
			        
				  int numRead = 0;
			      ByteArrayOutputStream baos = new ByteArrayOutputStream();

			      while ((numRead = in.read(buffer)) != -1) {
			            baos.write(buffer, 0, numRead);
			      }
			      JSONResponse response = new JSONResponse( new String(baos.toByteArray()));

		   	      AnodeClient.ResponseListener responseListener = anodeClient.getResponseListener();
		   	      if(responseListener != null){
		   	          responseListener.onResponse(response);
		   	      }
			        
			  }catch(Throwable t){
				  Log.e("AnodeHTTPClient.AnodeHTTPClientThread", "AnodeHTTPClient.AnodeHTTPClientThread.run()", t);
		      } finally {
		        //if (client != null) close();
		        
		            if (in != null){
		            	try{
		            		in.close();
		            	}catch(Throwable tt){
		            	}
		            }
		      }
			
		}
		
		
		
		
	}
	
	
	
}

