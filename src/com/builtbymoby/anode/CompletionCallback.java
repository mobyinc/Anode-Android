package com.builtbymoby.anode;

public abstract class CompletionCallback {
	public CompletionCallback() {}
	public abstract void done(Object objects);
	public abstract void fail(AnodeException e);
}
