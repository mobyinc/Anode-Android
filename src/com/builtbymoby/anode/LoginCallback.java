package com.builtbymoby.anode;

public abstract class LoginCallback extends AnodeCallback {
	public LoginCallback() {}
	public abstract void done(AnodeUser user);	
}
