package laf.misc;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPath;

@RequestScoped
public interface ActionContext {

	public abstract HttpServletResponse getResponse();

	public abstract HttpServletRequest getRequest();

	void setRequest(HttpServletRequest request);

	void setResponse(HttpServletResponse response);

	ActionPath<Object> getInvokedPath();

	void setInvokedPath(ActionPath<Object> invokedPath);

}
