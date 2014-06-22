package laf.component.core;

import java.io.Serializable;

import laf.component.pageScope.PageScoped;

/**
 * Contains various page related information
 */
@PageScoped
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	private ComponentView<?> view;

	public ComponentView<?> getView() {
		return view;
	}

	public void setView(ComponentView<?> view) {
		this.view = view;
	}
}
