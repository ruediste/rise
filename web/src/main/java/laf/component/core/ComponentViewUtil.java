package laf.component.core;

import javax.inject.Inject;

import laf.mvc.actionPath.ActionPathBuilderBase;
import laf.mvc.actionPath.ActionPathFactory;

public class ComponentViewUtil {

	@Inject
	ActionPathFactory actionPathFactory;

	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	public ActionPathBuilderBase path() {
		return actionPathFactory.buildActionPath();
	}
}
