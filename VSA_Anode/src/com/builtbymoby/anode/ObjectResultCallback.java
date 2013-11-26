package com.builtbymoby.anode;

public abstract class ObjectResultCallback {
	public ObjectResultCallback() {}
	public abstract void done(AnodeObject object);
	public abstract void fail(AnodeException e);
}
