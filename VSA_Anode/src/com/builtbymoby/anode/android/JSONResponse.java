package com.builtbymoby.anode.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;



/**
 * Ecapsulates a JSON response.
 */
public class JSONResponse {

	private JSONTokener tokener;
	private String responseString;

	/**
	 * 
	 * @param responseString
	 */
	public JSONResponse(String responseString){
		this.tokener = new JSONTokener(responseString);
		this.responseString = responseString;
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public boolean isJSONObject() throws JSONException{
		
        if(this.tokener.nextValue() instanceof JSONObject){
		    return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public boolean isJSONArray() throws JSONException{
		if(this.tokener.nextValue() instanceof JSONArray){
			    return true;
	    }
			
        return false;	
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getJSONObject() throws JSONException{
		if(this.tokener.nextValue() instanceof JSONObject){
	        return (JSONObject)this.tokener.nextValue();
		}
			
	    return null;
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getJSONArray() throws JSONException{
		
		try{
			if(this.tokener.nextValue() instanceof JSONArray){
				return new JSONArray(this.responseString);
			
			}
		
		}catch(Throwable t){
			
			Log.e("getJSONArray","getJSONArray",t);
		}
		
		return null;
	}
	
}
