package com.github.ruediste.laf.component.web.components;

import com.github.ruediste.laf.component.web.components.template.CReloadHtmlTemplate;

/**
 * Component representing a partial page reload context
 */
@DefaultTemplate(CReloadHtmlTemplate.class)
public class CReload extends MultiChildrenComponent<CReload> {

	private int reloadCount;

	public int getReloadCount() {
		return reloadCount;
	}

	public void setReloadCount(int reloadCount) {
		this.reloadCount = reloadCount;
	}
}
