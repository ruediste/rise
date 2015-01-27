package com.github.ruediste.laf.component.web.defaultConfiguration;

import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;

public interface InitialRequestParserCP extends
		ConfigurationParameter<RequestParser<HttpRequest>> {

}
