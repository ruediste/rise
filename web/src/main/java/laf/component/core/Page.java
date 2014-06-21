package laf.component.core;

import laf.component.pageScope.PageScoped;

/**
 * Contains various page related information
 */
@PageScoped
public class Page {

	private ComponentView<?> view;

	public ComponentView<?> getView() {
		return view;
	}

	public void setView(ComponentView<?> view) {
		this.view = view;
	}
}
