package com.github.ruediste.rise.component.reload;

import com.github.ruediste.rise.core.scopes.RequestScoped;

@RequestScoped
public class PageReloadRequest {
	private long pageNr;
	private long componentNr;

	public long getPageNr() {
		return pageNr;
	}

	public void setPageNr(long pageNr) {
		this.pageNr = pageNr;
	}

	public long getComponentNr() {
		return componentNr;
	}

	public void setComponentNr(long componentNr) {
		this.componentNr = componentNr;
	}
}
