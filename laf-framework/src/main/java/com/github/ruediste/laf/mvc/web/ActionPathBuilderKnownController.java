package com.github.ruediste.laf.mvc.web;

import com.google.common.base.Preconditions;

public class ActionPathBuilderKnownController<TController extends IControllerMvcWeb>
		extends
		MvcWebActionPathBuilderBase<ActionPathBuilderKnownController<TController>> {

	private Class<TController> controllerClass;

	@SuppressWarnings("unchecked")
	public <T extends IControllerMvcWeb> ActionPathBuilderKnownController<T> initialize(
			Class<T> controllerClass) {
		Preconditions.checkNotNull(controllerClass);
		this.controllerClass = (Class<TController>) controllerClass;
		return (ActionPathBuilderKnownController<T>) self();
	}

	public TController go() {
		Preconditions.checkState(controllerClass != null,
				"ActionPathBuilder not initialized");
		return go(controllerClass);
	}
}
