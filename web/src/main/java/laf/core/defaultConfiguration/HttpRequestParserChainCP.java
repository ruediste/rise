package laf.core.defaultConfiguration;

import laf.base.configuration.ConfigurationParameter;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParserChain;

public interface HttpRequestParserChainCP extends
		ConfigurationParameter<RequestParserChain<HttpRequest>> {

}
