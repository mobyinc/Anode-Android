package com.builtbymoby.anode;

public abstract class JsonResponseCallback {
	public JsonResponseCallback() {}
	public abstract void done(JsonResponse response);
	public abstract void fail(AnodeException e);
}
