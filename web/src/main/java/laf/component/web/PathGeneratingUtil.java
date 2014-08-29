package laf.component.web;

import laf.core.base.ActionResult;

public interface PathGeneratingUtil {

	ActionInvocationBuilder path();

	<T> T path(Class<T> controllerClass);

	String url(ActionResult result);
}
