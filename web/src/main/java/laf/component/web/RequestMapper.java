package laf.component.web;

import laf.component.core.ActionInvocation;
import laf.core.http.request.HttpRequest;

/**
 * Maper between {@link HttpRequest}s and {@link ActionInvocation}s.
 */
public interface RequestMapper {
	ActionInvocation<String> parse(HttpRequest request);

	HttpRequest generate(ActionInvocation<String> invocation);
}
