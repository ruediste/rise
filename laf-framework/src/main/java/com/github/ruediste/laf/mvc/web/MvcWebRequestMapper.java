package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.mvc.ActionPath;

public interface MvcWebRequestMapper {

	ActionPath<String> parse(HttpRequest request);

	HttpRequest generate(ActionPath<String> path);

}
