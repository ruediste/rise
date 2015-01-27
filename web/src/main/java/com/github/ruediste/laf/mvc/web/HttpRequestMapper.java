package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.mvc.core.ActionPath;

public interface HttpRequestMapper {

	ActionPath<String> parse(HttpRequest request);

	HttpRequest generate(ActionPath<String> path);

}
