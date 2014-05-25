package laf.httpRequestMapping;

import java.util.Collection;

import laf.actionPath.ActionPath;
import laf.configuration.ConfigurationValue;
import laf.httpRequest.HttpRequest;

/**
 * {@link ConfigurationValue} for the {@link HttpRequestMappingRule
 * MappingRules} used to map between {@link HttpRequest}s and {@link ActionPath}
 * s.
 */
public interface HttpRequestMappingRules extends
ConfigurationValue<Collection<HttpRequestMappingRule>> {

}
