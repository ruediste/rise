package com.github.ruediste.rise.component;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class ComponentRequestInfo {

	private HttpRenderResult closePageResult;

	public HttpRenderResult getClosePageResult() {
		return closePageResult;
	}

	public void setClosePageResult(HttpRenderResult closePageResult) {
		this.closePageResult = closePageResult;
	}

	public boolean isComponentRequest() {
		return isComponentRequest;
	}

	public void setComponentRequest(boolean isComponentRequest) {
		this.isComponentRequest = isComponentRequest;
	}

	private boolean isComponentRequest;
}
