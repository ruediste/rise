package com.github.ruediste.laf.component.web;

import com.github.ruediste.laf.component.core.PathActionInvocation;
import com.github.ruediste.laf.core.base.ActionResult;

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
