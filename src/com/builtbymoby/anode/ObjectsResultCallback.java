package com.builtbymoby.anode;

import java.util.List;

public abstract class ObjectsResultCallback extends AnodeCallback {
	public ObjectsResultCallback() {}
	public abstract void done(List<AnodeObject> objects);	
}
