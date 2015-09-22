package com.builtbymoby.anode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;

/**
 * HTTP Client singleton
 */
public class AnodeHttpClient {
	private static AnodeHttpClient instance = null;
	
	/**
	 * 
	 * @return the singleton instance of AnodeHTTPClient
	 */
	public static AnodeHttpClient getInstance() {
		if (AnodeHttpClient.instance == null) {
			AnodeHttpClient.instance = new AnodeHttpClient();
		}
		
		return AnodeHttpClient.instance;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public void perform(final HttpUriRequest httpRequest, final JsonResponseCallback callback) {
		AnodeHttpClientThread httpThread = new AnodeHttpClientThread(httpRequest, callback); 
		httpThread.start();
	}
	
	/**
	 * Thread class
	 */
	private class AnodeHttpClientThread extends Thread {
		
		private HttpUriRequest httpRequest;
		private JsonResponseCallback callback;
		
		public AnodeHttpClientThread(final HttpUriRequest httpRequest, final JsonResponseCallback callback) {
			this.httpRequest = httpRequest;
			this.callback = callback;
		}
		
		public void run() {

			HttpClient client = new DefaultHttpClient();
			InputStream inputStream = null;

			try {
				HttpResponse response = client.execute(httpRequest);				
				StatusLine status = response.getStatusLine();
				HttpStatusCode httpStatusCode = HttpStatusCode.fromInt(status.getStatusCode());
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();

				byte[] buffer = new byte[1024];
				int numRead = 0;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				JsonResponse jsonResponse = null;
				try {
					while ((numRead = inputStream.read(buffer)) != -1) {
						baos.write(buffer, 0, numRead);
					}
					
					String stringResponse = new String(baos.toByteArray());
					jsonResponse = new JsonResponse(stringResponse);
					
					if (jsonResponse.isValid()) {
						if (httpStatusCode == HttpStatusCode.OK) {
							callback.done(jsonResponse);
						} else if (jsonResponse.isJSONObject() && jsonResponse.getJSONObject().optJSONObject("error") != null)  {
							JSONObject errorObject = jsonResponse.getJSONObject().getJSONObject("error");
							String message = errorObject.optString("message", "unknown error");
							HttpStatusCode code = HttpStatusCode.fromInt(errorObject.optInt("code", 0));
							
							callback.fail(new AnodeException(AnodeException.SERVER_ERROR, code, message));
						} else {
							callback.fail(new AnodeException(AnodeException.SERVER_ERROR, httpStatusCode, "unknown server error"));
						}
					} else if (httpStatusCode == HttpStatusCode.OK) {
						callback.done(null); // empty response
					} else {
						callback.fail(new AnodeException(AnodeException.INVALID_JSON, httpStatusCode, "JSON parse error"));
					}
					
				} catch (Exception e) {
					callback.fail(new AnodeException(AnodeException.NETWORK_ERROR, httpStatusCode, "error parsing server response", e));
				}				
			} catch(Throwable t) {
				callback.fail(new AnodeException(AnodeException.NETWORK_ERROR, "network error", t));
			} finally {
				if (inputStream != null){
					try{
						inputStream.close();
					} catch(Throwable tt) {	
						// do nothing
					}
				}
			}
		}
	}
}

