package com.github.ruediste.laf.component.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.github.ruediste.laf.component.core.ControllerUtilBase;
import com.github.ruediste.laf.component.core.PathActionInvocation;
import com.github.ruediste.laf.component.web.*;
import com.github.ruediste.laf.core.base.ActionResult;

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
