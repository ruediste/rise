package com.github.ruediste.rise.api;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.mvc.MvcUtil;
import com.google.common.reflect.TypeToken;

public class ControllerMvc<TSelf extends ControllerMvc<TSelf>> implements
		IControllerMvc {

	@Inject
	MvcUtil util;
	private Class<TSelf> controllerClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ControllerMvc() {
		controllerClass = (Class) TypeToken.of(getClass())
				.resolveType(ControllerMvc.class.getTypeParameters()[0])
				.getRawType();
	}

	protected <TView extends ViewMvc<?, TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {
		return util.view(viewClass, data);
	}

	protected ActionResult redirect(ActionResult path) {
		return util.redirect(path);
	}

	public TSelf go() {
		return util.path().go(controllerClass);
	}

	public <T> T go(Class<T> clazz) {
		return util.path().go(clazz);
	}

	public ActionInvocationBuilderKnownController<TSelf> path() {
		return util.path(controllerClass);
	}

	public void commit() {
		util.commit();
	}
}
