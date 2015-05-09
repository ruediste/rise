package com.github.ruediste.rise.mvc;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.scopes.RequestScoped;

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
