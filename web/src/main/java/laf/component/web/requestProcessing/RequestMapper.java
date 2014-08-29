package laf.component.web.requestProcessing;

import laf.component.core.ActionInvocation;
import laf.core.http.request.HttpRequest;

public interface RequestMapper {
	ActionInvocation<String> parse(HttpRequest request);

	HttpRequest generate(ActionInvocation<String> invocation);
}
