package com.github.ruediste.laf.component;

import com.github.ruediste.laf.core.scopes.RequestScoped;
import com.github.ruediste.laf.core.web.HttpRenderResult;

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
