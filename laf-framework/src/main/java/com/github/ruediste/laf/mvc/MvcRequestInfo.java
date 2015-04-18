package com.github.ruediste.laf.mvc;

import com.github.ruediste.laf.core.scopes.RequestScoped;

@RequestScoped
public class MvcRequestInfo {

	private ActionInvocation<Object> objectActionPath;

	public ActionInvocation<Object> getObjectActionPath() {
		return objectActionPath;
	}

	public void setObjectActionPath(ActionInvocation<Object> objectActionPath) {
		this.objectActionPath = objectActionPath;
	}
}
