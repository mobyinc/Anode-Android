package com.builtbymoby.anode;

import java.util.List;

public abstract class ObjectsResultCallback {
	public ObjectsResultCallback() {}
	public abstract void done(List<AnodeObject> objects);
	public abstract void fail(AnodeException e);
}
