package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.mvc.ActionInvocation;

public interface MvcWebRequestMapper {

	ActionInvocation<String> parse(HttpRequest request);

	HttpRequest generate(ActionInvocation<String> path);

}
