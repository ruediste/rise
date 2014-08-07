package laf.core.http.requestMapping;

import java.util.Deque;

import laf.base.configuration.ConfigurationParameter;
import laf.core.actionPath.ActionPath;
import laf.core.http.request.HttpRequest;

/**
 * {@link ConfigurationParameter} for the {@link HttpRequestMappingRule
 * MappingRules} used to map between {@link HttpRequest}s and {@link ActionPath}
 * s.
 */
public interface HttpRequestMappingRules extends
		ConfigurationParameter<Deque<HttpRequestMappingRule>> {

}
