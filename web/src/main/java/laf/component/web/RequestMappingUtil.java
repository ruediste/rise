package laf.component.web;

import javax.inject.Inject;

import laf.component.core.ActionInvocation;
import laf.core.http.request.HttpRequest;

public class RequestMappingUtil {

	@Inject
	public WebRequestInfo webRequestInfo;

	public HttpRequest generate(ActionInvocation<Object> invocation) {
		return webRequestInfo.getRequestMapper().generate(
				invocation.map(webRequestInfo.getArgumentSerializerChain()
						.generateFunction()));
	}
}
