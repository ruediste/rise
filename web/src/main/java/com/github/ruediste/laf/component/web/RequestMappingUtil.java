package com.github.ruediste.laf.component.web;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.ActionInvocation;
import com.github.ruediste.laf.core.http.request.HttpRequest;

public class RequestMappingUtil {

	@Inject
	public WebRequestInfo webRequestInfo;

	public HttpRequest generate(ActionInvocation<Object> invocation) {
		return webRequestInfo.getRequestMapper().generate(
				invocation.map(webRequestInfo.getArgumentSerializerChain()::generate));
	}
}
