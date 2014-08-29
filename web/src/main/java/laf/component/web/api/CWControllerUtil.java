package laf.component.web.api;

import javax.inject.Inject;

import laf.component.core.ControllerUtilBase;
import laf.component.web.*;
import laf.core.base.ActionResult;

public class CWControllerUtil extends ControllerUtilBase implements
		PathGeneratingUtil {

	@Inject
	PathGeneratingUtilImpl pathGeneratingUtilImpl;

	@Override
	public ActionInvocationBuilder path() {
		return pathGeneratingUtilImpl.path();
	}

	@Override
	public <T> T path(Class<T> controllerClass) {
		return pathGeneratingUtilImpl.path(controllerClass);
	}

	@Override
	public String url(ActionResult result) {
		return pathGeneratingUtilImpl.url(result);
	}
}
