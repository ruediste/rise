package laf.core.http.requestMapping;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import laf.base.BaseModule;
import laf.core.actionPath.ActionPathModule;
import laf.core.controllerInfo.ControllerInfoModule;
import laf.core.http.request.HttpRequestModule;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProviderModule;
import laf.core.http.requestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

/**
 * URLs have to be mapped to action method invocations for request handling, and
 * action method invocations have to be mapped to URLs for URL generation.
 *
 * <p>
 * During this round trip, different representations of the parameter values are
 * necessary. When parsing an URL, entity parameters may not be loaded from the
 * database, since the controller and the action method have an influence on the
 * transaction and {@link EntityManager} which is to be used. Thus parameters
 * are represented as {@link ParameterValueProvider}s after parsing.
 * </p>
 * <p>
 * <img src="doc-files/RequestRepresentations.png" />
 * </p>
 *
 * <p>
 * When generating URLs, the parameters are at first represented by original
 * parameter objects, typically get transformed to strings and are finally
 * combined to the URL.
 * </p>
 *
 * <p>
 * A list of {@link HttpRequestMappingRule}s is used to perform the actual
 * mapping, allowing total customization thereof. The list is configured via
 * {@link #httpRequestMappingRules}
 * </p>
 *
 * <p>
 * There is quite a lot of functionality shared between different mapping rules.
 * Care has been taken to make this functionality reusable and to split it into
 * meaningful sub packages. Since most rules will separate the string
 * representation of the parameter values and the combination thereof to the
 * final URL, the {@link TwoStageMappingRuleModule} has been created.
 * </p>
 */
@Module(description = "Maps URLs to ActionPaths using UrlMappingRules", exported = {
		ActionPathModule.class, ControllerInfoModule.class,
		ParameterValueProviderModule.class, HttpRequestModule.class }, imported = { BaseModule.class })
@Singleton
public class HttpRequestMappingModule {

}
