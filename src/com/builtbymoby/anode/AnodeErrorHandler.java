package com.builtbymoby.anode;

public abstract class AnodeErrorHandler {

	public AnodeErrorHandler() {
		
	}
	
	public void handleError(AnodeException e) {
		onError(e);
		
		// TODO: filter these
		onFilteredError(e);
	}
	
	public abstract void onFilteredError(AnodeException e);
	
	public void onError(AnodeException e) {
		
	}
}
