package com.github.ruediste.rise.test;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.mvc.MvcUtil;

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

	public <T extends IControllerMvc> T go(Class<T> controllerClass) {
		return util.path(controllerClass).go();
	}
}
