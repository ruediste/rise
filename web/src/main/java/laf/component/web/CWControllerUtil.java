package laf.component.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import laf.component.core.ControllerUtilBase;
import laf.component.core.PathActionInvocation;
import laf.component.web.*;
import laf.core.base.ActionResult;

@RequestScoped
public class CWControllerUtil extends ControllerUtilBase implements
		PathGeneratingUtil {

	@Inject
	RequestMappingUtil requestMappingUtil;

	@Inject
	PathGeneratingUtilDependencies pathGeneratingUtilDependencies;

	@Override
	public PathGeneratingUtilDependencies getPathGeneratingUtilDependencies() {
		return pathGeneratingUtilDependencies;
	}

	public void setDestination(ActionResult target) {
		PathActionInvocation invocation = (PathActionInvocation) target;
		setDestinationUrl(httpService.url(requestMappingUtil.generate(
				invocation).getPathWithParameters()));
	}
}
