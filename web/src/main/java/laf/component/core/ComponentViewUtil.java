package laf.component.core;

import javax.inject.Inject;

import laf.mvc.actionPath.ActionPathFactory;
import laf.mvc.actionPath.ActionPathFactory.ActionPathBuilder;

public class ComponentViewUtil {

	@Inject
	ActionPathFactory actionPathFactory;

	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	public ActionPathBuilder path() {
		return actionPathFactory.buildActionPath();
	}
}
