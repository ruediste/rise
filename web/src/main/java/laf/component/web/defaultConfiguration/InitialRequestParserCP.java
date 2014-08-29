package laf.component.web.defaultConfiguration;

import laf.core.base.configuration.ConfigurationParameter;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParser;

public interface InitialRequestParserCP extends
		ConfigurationParameter<RequestParser<HttpRequest>> {

}
