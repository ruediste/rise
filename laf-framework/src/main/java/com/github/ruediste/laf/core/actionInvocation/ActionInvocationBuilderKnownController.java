package com.github.ruediste.laf.core.actionInvocation;

import com.github.ruediste.laf.api.IController;
import com.google.common.base.Preconditions;

public class ActionInvocationBuilderKnownController<TController extends IController>
		extends
		ActionInvocationBuilderBase<ActionInvocationBuilderKnownController<TController>> {

	private Class<TController> controllerClass;

	@SuppressWarnings("unchecked")
	public <T extends IController> ActionInvocationBuilderKnownController<T> initialize(
			Class<T> controllerClass) {
		Preconditions.checkNotNull(controllerClass);
		this.controllerClass = (Class<TController>) controllerClass;
		return (ActionInvocationBuilderKnownController<T>) self();
	}

	public TController go() {
		Preconditions.checkState(controllerClass != null,
				"ActionPathBuilder not initialized");
		return go(controllerClass);
	}
}
