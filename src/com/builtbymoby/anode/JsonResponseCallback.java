package com.builtbymoby.anode;

public abstract class JsonResponseCallback extends AnodeCallback {
	public JsonResponseCallback() {}
	public abstract void done(JsonResponse response);	
}
