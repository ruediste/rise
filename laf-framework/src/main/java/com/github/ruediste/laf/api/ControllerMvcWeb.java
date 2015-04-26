package com.github.ruediste.laf.api;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.web.ActionPathBuilderKnownController;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcWebControllerUtil;
import com.google.common.reflect.TypeToken;

public class ControllerMvcWeb<TSelf extends ControllerMvcWeb<TSelf>> implements
		IControllerMvcWeb {

	@Inject
	MvcWebControllerUtil util;
	private Class<TSelf> controllerClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ControllerMvcWeb() {
		controllerClass = (Class) TypeToken.of(getClass())
				.resolveType(ControllerMvcWeb.class.getTypeParameters()[0])
				.getRawType();
	}

	protected <TView extends ViewMvcWeb<?, TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {
		return util.view(viewClass, data);
	}

	protected ActionResult redirect(ActionResult path) {
		return util.redirect(path);
	}

	public TSelf go() {
		return util.path().go(controllerClass);
	}

	public ActionPathBuilderKnownController<TSelf> path() {
		return util.path(controllerClass);
	}

}
