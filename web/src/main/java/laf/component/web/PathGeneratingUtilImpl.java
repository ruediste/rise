package laf.component.web;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.PathActionInvocation;
import laf.core.base.ActionResult;

public class PathGeneratingUtilImpl implements PathGeneratingUtil {
	@Inject
	Instance<ActionInvocationBuilder> builderInstance;

	@Inject
	RequestMappingUtil requestMappingUtil;

	@Override
	public ActionInvocationBuilder path() {
		return builderInstance.get();
	}

	@Override
	public <T> T path(Class<T> controllerClass) {
		return builderInstance.get().controller(controllerClass);
	}

	@Override
	public String url(ActionResult result) {
		return requestMappingUtil.generate((PathActionInvocation) result)
				.getPathWithParameters();
	}
}
