package laf.mvc.web.defaultConfiguration;

import laf.base.configuration.ConfigurationParameter;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParser;

public interface RequestParserCP extends
		ConfigurationParameter<RequestParser<HttpRequest>> {

}