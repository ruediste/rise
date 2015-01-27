package com.github.ruediste.laf.component.core;

import java.io.Serializable;

import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.core.pageScope.PageScoped;

/**
 * Contains various page related information
 */
@PageScoped
public class PageInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private CView<?> view;
	private Object controller;

	public CView<?> getView() {
		return view;
	}

	public void setView(CView<?> view) {
		this.view = view;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}
}
