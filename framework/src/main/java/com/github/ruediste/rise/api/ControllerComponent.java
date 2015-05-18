package com.github.ruediste.rise.api;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.IControllerComponent;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;

public class ControllerComponent implements IControllerComponent {

	@Inject
	ComponentUtil util;

	public <T extends IController> T go(Class<T> controllerClass) {
		return util.go(controllerClass);
	}

	public <T extends IController> ActionInvocationBuilderKnownController<T> path(
			Class<T> controllerClass) {
		return util.path(controllerClass);
	}

	public ActionInvocationBuilder path() {
		return util.path();
	}

	public void commit() {
		util.commit();
	}

	public void commit(Runnable inTransaction) {
		util.commit(inTransaction);
	}

	public void checkAndCommit(Runnable checker) {
		util.checkAndCommit(checker);
	}

	public void checkAndCommit(Runnable checker, Runnable inTransaction) {
		util.checkAndCommit(checker, inTransaction);
	}

}
