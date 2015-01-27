package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.base.ActionResult;

public interface MWControllerUtil {

	<TView extends MvcWebView<TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data);

	ActionResult redirect(ActionResult path);
}
