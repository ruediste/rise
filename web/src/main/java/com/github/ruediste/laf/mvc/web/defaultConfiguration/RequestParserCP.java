package com.github.ruediste.laf.mvc.web.defaultConfiguration;

import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;

public interface RequestParserCP extends
		ConfigurationParameter<RequestParser<HttpRequest>> {

}