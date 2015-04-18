package com.github.ruediste.laf.mvc;

/**
 * Represent string values attached to {@link ActionInvocation}s. These values are
 * transferred along with the ActionPath to the client and back again.
 */
public class ActionInvocationParameter {

	private final String name;

	public ActionInvocationParameter(String name) {
		this.name = name;

	}

	public void set(ActionInvocation<?> path, String value) {
		path.parameters.put(this, value);
	}

	public String get(ActionInvocation<?> path) {
		return path.parameters.get(this);
	}

	public String getName() {
		return name;
	}
}
