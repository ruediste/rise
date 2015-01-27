package com.github.ruediste.laf.mvc.core;

import javax.enterprise.context.RequestScoped;

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
