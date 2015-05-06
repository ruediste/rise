package com.github.ruediste.laf.test;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.CoreUtil;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcUtil;

public class IntegrationTestUtil {

	private String baseUrl;

	public void initialize(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Inject
	MvcUtil util;

	@Inject
	CoreUtil coreUtil;

	public String url(ActionResult result) {
		return baseUrl
				+ coreUtil.toPathInfo((ActionInvocationResult) result)
						.getValue();
	}

	public <T extends IControllerMvcWeb> T go(Class<T> controllerClass) {
		return util.path(controllerClass).go();
	}
}
