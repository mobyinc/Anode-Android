package com.builtbymoby.anode;

public abstract class CompletionCallback {
	public CompletionCallback() {}
	public abstract void done(Object object);
	public abstract void fail(AnodeException e);
}
