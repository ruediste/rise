package laf.http.requestMapping;

import java.util.Deque;

import laf.actionPath.ActionPath;
import laf.base.configuration.ConfigurationParameter;
import laf.http.request.HttpRequest;

/**
 * {@link ConfigurationParameter} for the {@link HttpRequestMappingRule
 * MappingRules} used to map between {@link HttpRequest}s and {@link ActionPath}
 * s.
 */
public interface HttpRequestMappingRules extends
		ConfigurationParameter<Deque<HttpRequestMappingRule>> {

}
