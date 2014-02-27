package com.builtbymoby.anode;

public abstract class CompletionCallback extends AnodeCallback {
	public CompletionCallback() {}
	public abstract void done(Object object);
}
