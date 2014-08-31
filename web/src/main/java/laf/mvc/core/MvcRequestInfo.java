package laf.mvc.core;

import javax.enterprise.context.RequestScoped;

import laf.mvc.core.actionPath.ActionPath;

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
