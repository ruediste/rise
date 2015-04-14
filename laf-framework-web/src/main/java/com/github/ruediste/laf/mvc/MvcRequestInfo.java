package com.github.ruediste.laf.mvc;

import com.github.ruediste.laf.core.scopes.RequestScoped;

@RequestScoped
public class MvcRequestInfo {

	private ActionPath<Object> objectActionPath;

	public ActionPath<Object> getObjectActionPath() {
		return objectActionPath;
	}

	public void setObjectActionPath(ActionPath<Object> objectActionPath) {
		this.objectActionPath = objectActionPath;
	}
}
