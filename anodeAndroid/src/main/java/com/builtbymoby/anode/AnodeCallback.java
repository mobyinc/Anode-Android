package com.builtbymoby.anode;

public abstract class AnodeCallback {
	public AnodeCallback() {}
	public void fail(AnodeException e) {
		if (Anode.getErrorHandler() != null) {
			Anode.getErrorHandler().handleError(e);
		}
	}
}

