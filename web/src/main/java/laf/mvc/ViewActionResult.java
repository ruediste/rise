package laf.mvc;

import laf.base.ActionResult;

public class ViewActionResult<TView extends View<TData>, TData> implements
		ActionResult {

	private final Class<TView> viewClass;
	private final TData data;

	public ViewActionResult(Class<TView> viewClass, TData data) {
		this.viewClass = viewClass;
		this.data = data;

		// render the view immediately

	}

	public Class<TView> getViewClass() {
		return viewClass;
	}

	public TData getData() {
		return data;
	}
}
