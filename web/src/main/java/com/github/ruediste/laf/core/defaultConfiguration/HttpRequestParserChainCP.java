package com.github.ruediste.laf.core.defaultConfiguration;

import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParserChain;

public interface HttpRequestParserChainCP extends
		ConfigurationParameter<RequestParserChain<HttpRequest>> {

}
