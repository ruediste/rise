package laf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.urlMapping.ActionPath;

public class ActionContextImpl implements ActionContext {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ActionPath<Object> invokedPath;

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public ActionPath<Object> getInvokedPath() {
		return invokedPath;
	}

	@Override
	public void setInvokedPath(ActionPath<Object> invokedPath) {
		this.invokedPath = invokedPath;
	}
}
