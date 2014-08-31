package laf.component.web;

import laf.component.core.PathActionInvocation;
import laf.core.base.ActionResult;

public interface PathGeneratingUtil {

	PathGeneratingUtilDependencies getPathGeneratingUtilDependencies();

	default ActionInvocationBuilder path() {
		return getPathGeneratingUtilDependencies().builderInstance.get();
	}

	default <T> T path(Class<T> controllerClass) {
		return getPathGeneratingUtilDependencies().builderInstance.get()
				.controller(controllerClass);
	}

	default String url(ActionResult result) {
		return url(getPathGeneratingUtilDependencies().requestMappingUtil
				.generate((PathActionInvocation) result)
				.getPathWithParameters());
	}

	default String url(String path) {
		return getPathGeneratingUtilDependencies().service.url(path);
	}
}
