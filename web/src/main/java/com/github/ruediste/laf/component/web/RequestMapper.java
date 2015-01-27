package com.github.ruediste.laf.component.web;

import com.github.ruediste.laf.component.core.ActionInvocation;
import com.github.ruediste.laf.core.http.request.HttpRequest;

/**
 * Maper between {@link HttpRequest}s and {@link ActionInvocation}s.
 */
public interface RequestMapper {
	ActionInvocation<String> parse(HttpRequest request);

	HttpRequest generate(ActionInvocation<String> invocation);
}
