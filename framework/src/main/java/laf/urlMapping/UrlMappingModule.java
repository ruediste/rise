package laf.urlMapping;

import java.util.ArrayDeque;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import laf.LAF;
import laf.actionPath.ActionPathModule;
import laf.configuration.ConfigurationModule;
import laf.configuration.ConfigurationParameter;
import laf.controllerInfo.ControllerInfoModule;
import laf.initialization.InitializationModule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;
import laf.urlMapping.parameterValueProvider.ParameterValueProviderModule;

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
 *
 * <p>
 * When representing an invocation to an action method, the parameters are at
 * first represented by original parameter objects, get transformed to strings
 * afterwards and are finally combined to the URL.
 * </p>
 *
 * <p>
 * A list of {@link UrlMappingRule}s is used to perform the actual mapping,
 * allowing total customization thereof. The list is configured via
 * {@link LAF#getUrlMappingRules()}
 * </p>
 */
@Module(description = "Maps URLs to ActionPaths using UrlMappingRules", exported = {
		ActionPathModule.class, ControllerInfoModule.class,
		ParameterValueProviderModule.class }, imported = {
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class UrlMappingModule {

	public ConfigurationParameter<ArrayDeque<UrlMappingRule>> urlMappingRules = new ConfigurationParameter<ArrayDeque<UrlMappingRule>>(
			new ArrayDeque<UrlMappingRule>());
}
