package com.github.ruediste.laf.api;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcWebControllerUtil;

public class ControllerMvcWeb implements IControllerMvcWeb {

	@Inject
	MvcWebControllerUtil util;

	protected <TView extends ViewMvcWeb<TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {
		return util.view(viewClass, data);
	}

	protected ActionResult redirect(ActionResult path) {
		return util.redirect(path);
	}

}
