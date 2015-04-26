package com.github.ruediste.laf.test;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.InvocationActionResult;
import com.github.ruediste.laf.mvc.web.ActionInvocationUtil;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcWebRenderUtil;

public class IntegrationTestUtil {

	private String baseUrl;

	public void initialize(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Inject
	MvcWebRenderUtil util;

	@Inject
	ActionInvocationUtil actionInvocationUtil;

	public String url(ActionResult result) {
		return baseUrl
				+ actionInvocationUtil.toPathInfo(
						(InvocationActionResult) result).getValue();
	}

	public <T extends IControllerMvcWeb> T path(Class<T> controllerClass) {
		return util.path(controllerClass);
	}
}
