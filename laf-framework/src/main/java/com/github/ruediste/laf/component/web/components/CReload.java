package com.github.ruediste.laf.component.web.components;

/**
 * Component representing a partial page reload context
 */
public class CReload extends MultiChildrenComponent<CReload> {

	private int reloadCount;

	public int getReloadCount() {
		return reloadCount;
	}

	public void setReloadCount(int reloadCount) {
		this.reloadCount = reloadCount;
	}
}
