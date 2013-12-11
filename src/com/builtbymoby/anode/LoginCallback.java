package com.builtbymoby.anode;

public abstract class LoginCallback {
	public LoginCallback() {}
	public abstract void done(AnodeUser user);
	public abstract void fail(AnodeException e);
}
